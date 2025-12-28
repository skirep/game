// src/controls/touch.js
// Gestió de controls tàctils per dispositius mòbils

export class TouchControls {
    constructor() {
        this.enabled = false;
        
        // Joystick
        this.joystickContainer = null;
        this.joystick = null;
        this.joystickActive = false;
        this.joystickStartPos = { x: 0, y: 0 };
        this.joystickCurrentPos = { x: 0, y: 0 };
        this.joystickMaxDistance = 50;
        this.joystickTouchId = null;
        
        // Zona de mirar (touch pan)
        this.lookTouchId = null;
        this.lastLookPos = { x: 0, y: 0 };
        
        // Callbacks
        this.onMove = null;
        this.onRotate = null;
        this.onShoot = null;
        this.onPause = null;
        
        this._setupElements();
        this._setupListeners();
    }

    // Configura els elements HTML
    _setupElements() {
        this.touchControls = document.getElementById('touchControls');
        this.joystickContainer = document.getElementById('joystickContainer');
        this.joystick = document.getElementById('joystick');
        this.fireButton = document.getElementById('fireButton');
        this.pauseButton = document.getElementById('pauseButtonTouch');
    }

    // Configura els event listeners
    _setupListeners() {
        // Joystick
        if (this.joystickContainer) {
            this.joystickContainer.addEventListener('touchstart', (e) => {
                if (!this.enabled) return;
                e.preventDefault();
                this._handleJoystickStart(e.touches[0]);
            }, { passive: false });
        }

        // Botó de disparo
        if (this.fireButton) {
            this.fireButton.addEventListener('touchstart', (e) => {
                if (!this.enabled) return;
                e.preventDefault();
                if (this.onShoot) {
                    this.onShoot();
                }
            }, { passive: false });
        }

        // Botó de pausa
        if (this.pauseButton) {
            this.pauseButton.addEventListener('touchstart', (e) => {
                if (!this.enabled) return;
                e.preventDefault();
                if (this.onPause) {
                    this.onPause();
                }
            }, { passive: false });
        }

        // Touch move global (per joystick i mirar)
        document.addEventListener('touchmove', (e) => {
            if (!this.enabled) return;
            
            for (let i = 0; i < e.touches.length; i++) {
                const touch = e.touches[i];
                
                // Joystick
                if (this.joystickTouchId === touch.identifier) {
                    e.preventDefault();
                    this._handleJoystickMove(touch);
                }
                // Mirar
                else if (this.lookTouchId === touch.identifier) {
                    e.preventDefault();
                    this._handleLookMove(touch);
                }
            }
        }, { passive: false });

        // Touch end global
        document.addEventListener('touchend', (e) => {
            if (!this.enabled) return;
            
            for (let i = 0; i < e.changedTouches.length; i++) {
                const touch = e.changedTouches[i];
                
                if (this.joystickTouchId === touch.identifier) {
                    this._handleJoystickEnd();
                } else if (this.lookTouchId === touch.identifier) {
                    this._handleLookEnd();
                }
            }
        });

        // Touch start global (per mirar)
        document.addEventListener('touchstart', (e) => {
            if (!this.enabled) return;
            
            for (let i = 0; i < e.touches.length; i++) {
                const touch = e.touches[i];
                const target = document.elementFromPoint(touch.clientX, touch.clientY);
                
                // Si no és el joystick ni cap botó, és per mirar
                if (target && 
                    !this._isControlElement(target) &&
                    this.lookTouchId === null) {
                    this.lookTouchId = touch.identifier;
                    this.lastLookPos = { x: touch.clientX, y: touch.clientY };
                }
            }
        });
    }

    // Comprova si un element és un control
    _isControlElement(element) {
        return element === this.joystickContainer ||
               element === this.joystick ||
               element === this.fireButton ||
               element === this.pauseButton ||
               this.joystickContainer?.contains(element);
    }

    // Gestió del joystick - inici
    _handleJoystickStart(touch) {
        this.joystickTouchId = touch.identifier;
        this.joystickActive = true;
        
        const rect = this.joystickContainer.getBoundingClientRect();
        this.joystickStartPos = {
            x: rect.left + rect.width / 2,
            y: rect.top + rect.height / 2
        };
    }

    // Gestió del joystick - moviment
    _handleJoystickMove(touch) {
        if (!this.joystickActive) return;

        const deltaX = touch.clientX - this.joystickStartPos.x;
        const deltaY = touch.clientY - this.joystickStartPos.y;
        
        const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        const angle = Math.atan2(deltaY, deltaX);
        
        const limitedDistance = Math.min(distance, this.joystickMaxDistance);
        
        this.joystickCurrentPos = {
            x: Math.cos(angle) * limitedDistance,
            y: Math.sin(angle) * limitedDistance
        };
        
        // Actualitzar posició visual del joystick
        this.joystick.style.transform = `translate(calc(-50% + ${this.joystickCurrentPos.x}px), calc(-50% + ${this.joystickCurrentPos.y}px))`;
        
        // Callback de moviment
        if (this.onMove) {
            const normalizedX = this.joystickCurrentPos.x / this.joystickMaxDistance;
            const normalizedY = this.joystickCurrentPos.y / this.joystickMaxDistance;
            this.onMove(normalizedX, -normalizedY);
        }
    }

    // Gestió del joystick - final
    _handleJoystickEnd() {
        this.joystickActive = false;
        this.joystickTouchId = null;
        this.joystickCurrentPos = { x: 0, y: 0 };
        
        // Reset visual
        this.joystick.style.transform = 'translate(-50%, -50%)';
        
        // Callback de moviment (stop)
        if (this.onMove) {
            this.onMove(0, 0);
        }
    }

    // Gestió de mirar - moviment
    _handleLookMove(touch) {
        const deltaX = touch.clientX - this.lastLookPos.x;
        const deltaY = touch.clientY - this.lastLookPos.y;
        
        this.lastLookPos = { x: touch.clientX, y: touch.clientY };
        
        if (this.onRotate) {
            this.onRotate(deltaX * 0.005, -deltaY * 0.005);
        }
    }

    // Gestió de mirar - final
    _handleLookEnd() {
        this.lookTouchId = null;
    }

    // Habilita els controls
    enable() {
        this.enabled = true;
        if (this.touchControls) {
            this.touchControls.style.display = 'block';
        }
    }

    // Deshabilita els controls
    disable() {
        this.enabled = false;
        if (this.touchControls) {
            this.touchControls.style.display = 'none';
        }
        this._handleJoystickEnd();
        this._handleLookEnd();
    }

    // Detecta si és un dispositiu mòbil
    static isMobile() {
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    }
}
