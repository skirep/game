// src/controls/keyboard.js
// Gestió de controls de teclat

export class KeyboardControls {
    constructor() {
        this.keys = {};
        this.enabled = false;
        
        this._setupListeners();
    }

    // Configura els event listeners
    _setupListeners() {
        document.addEventListener('keydown', (e) => {
            if (this.enabled) {
                this.keys[e.key.toLowerCase()] = true;
                this.keys[e.code] = true;
            }
        });

        document.addEventListener('keyup', (e) => {
            this.keys[e.key.toLowerCase()] = false;
            this.keys[e.code] = false;
        });
    }

    // Habilita els controls
    enable() {
        this.enabled = true;
    }

    // Deshabilita els controls
    disable() {
        this.enabled = false;
        this.keys = {};
    }

    // Comprova si una tecla està premuda
    isPressed(key) {
        return this.keys[key.toLowerCase()] || this.keys[key] || false;
    }

    // Obté la direcció de moviment basada en WASD
    getMoveDirection() {
        const direction = { x: 0, z: 0 };

        if (this.isPressed('w') || this.isPressed('ArrowUp')) {
            direction.z = 1;
        }
        if (this.isPressed('s') || this.isPressed('ArrowDown')) {
            direction.z = -1;
        }
        if (this.isPressed('a') || this.isPressed('ArrowLeft')) {
            direction.x = -1;
        }
        if (this.isPressed('d') || this.isPressed('ArrowRight')) {
            direction.x = 1;
        }

        return direction;
    }

    // Comprova si s'ha premut ESC
    isPausePressed() {
        return this.isPressed('Escape');
    }
}
