// src/controls/mouse.js
// Gestió de controls del ratolí

export class MouseControls {
    constructor(canvas) {
        this.canvas = canvas;
        this.enabled = false;
        this.sensitivity = 0.002;
        
        this.onRotate = null;
        this.onShoot = null;
        
        this._setupListeners();
    }

    // Configura els event listeners
    _setupListeners() {
        // Moviment del ratolí
        document.addEventListener('mousemove', (e) => {
            if (this.enabled && document.pointerLockElement === this.canvas) {
                const movementX = e.movementX || e.mozMovementX || e.webkitMovementX || 0;
                const movementY = e.movementY || e.mozMovementY || e.webkitMovementY || 0;
                
                if (this.onRotate) {
                    this.onRotate(
                        movementX * this.sensitivity,
                        -movementY * this.sensitivity
                    );
                }
            }
        });

        // Click del ratolí
        this.canvas.addEventListener('click', () => {
            if (this.enabled && document.pointerLockElement === this.canvas) {
                if (this.onShoot) {
                    this.onShoot();
                }
            }
        });
    }

    // Habilita els controls
    enable() {
        this.enabled = true;
    }

    // Deshabilita els controls
    disable() {
        this.enabled = false;
    }

    // Estableix la sensibilitat
    setSensitivity(value) {
        this.sensitivity = value;
    }
}
