// src/engine/scene.js
// Motor principal de l'escena 3D utilitzant Babylon.js

export class GameScene {
    constructor() {
        this.canvas = document.getElementById('renderCanvas');
        this.engine = null;
        this.scene = null;
        this.camera = null;
        this.light = null;
        this.shadowGenerator = null;
    }

    // Inicialitza l'escena de Babylon.js
    init() {
        // Crear motor de Babylon.js
        this.engine = new BABYLON.Engine(this.canvas, true, {
            preserveDrawingBuffer: true,
            stencil: true
        });

        // Crear escena
        this.scene = new BABYLON.Scene(this.engine);
        this.scene.clearColor = new BABYLON.Color3(0.1, 0.1, 0.1);
        
        // Activar col·lisions
        this.scene.collisionsEnabled = true;
        this.scene.gravity = new BABYLON.Vector3(0, -0.15, 0);

        // Crear càmera en primera persona
        this.camera = new BABYLON.UniversalCamera(
            "camera",
            new BABYLON.Vector3(0, 1.6, 0),
            this.scene
        );
        this.camera.attachControl(this.canvas, true);
        
        // Configuració de la càmera
        this.camera.speed = 0.15;
        this.camera.angularSensibility = 2000;
        this.camera.checkCollisions = true;
        this.camera.applyGravity = false;
        this.camera.ellipsoid = new BABYLON.Vector3(0.5, 0.8, 0.5);
        
        // Deshabilitar controls per defecte (els gestionarem nosaltres)
        this.camera.inputs.clear();

        // Llum ambient
        const ambientLight = new BABYLON.HemisphericLight(
            "ambientLight",
            new BABYLON.Vector3(0, 1, 0),
            this.scene
        );
        ambientLight.intensity = 0.6;

        // Llum direccional per ombres
        this.light = new BABYLON.DirectionalLight(
            "dirLight",
            new BABYLON.Vector3(-1, -2, -1),
            this.scene
        );
        this.light.position = new BABYLON.Vector3(20, 40, 20);
        this.light.intensity = 0.7;

        // Generador d'ombres
        this.shadowGenerator = new BABYLON.ShadowGenerator(1024, this.light);
        this.shadowGenerator.useBlurExponentialShadowMap = true;
        this.shadowGenerator.blurKernel = 32;

        // Optimitzacions per mòbil
        if (this._isMobile()) {
            this.engine.setHardwareScalingLevel(1.5);
            this.shadowGenerator.mapSize = 512;
        }

        return this;
    }

    // Detecta si és un dispositiu mòbil
    _isMobile() {
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    }

    // Inicia el bucle de renderitzat
    startRenderLoop(callback) {
        this.engine.runRenderLoop(() => {
            if (callback) {
                callback();
            }
            this.scene.render();
        });

        // Ajustar canvas quan es redimensiona la finestra
        window.addEventListener('resize', () => {
            this.engine.resize();
        });
    }

    // Atura el motor
    dispose() {
        if (this.scene) {
            this.scene.dispose();
        }
        if (this.engine) {
            this.engine.dispose();
        }
    }

    // Activa o desactiva el pointer lock
    requestPointerLock() {
        if (!this._isMobile()) {
            this.canvas.requestPointerLock = this.canvas.requestPointerLock ||
                                             this.canvas.mozRequestPointerLock ||
                                             this.canvas.webkitRequestPointerLock;
            if (this.canvas.requestPointerLock) {
                this.canvas.requestPointerLock();
            }
        }
    }

    exitPointerLock() {
        document.exitPointerLock = document.exitPointerLock ||
                                    document.mozExitPointerLock ||
                                    document.webkitExitPointerLock;
        if (document.exitPointerLock) {
            document.exitPointerLock();
        }
    }
}
