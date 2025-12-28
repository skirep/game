// src/engine/game-loop.js
// Bucle principal del joc i gestió de l'estat

export class GameLoop {
    constructor(gameScene) {
        this.gameScene = gameScene;
        this.isPaused = false;
        this.isGameOver = false;
        this.isVictory = false;
        this.currentLevel = 1;
        this.maxLevels = 3;
        
        // Callbacks
        this.onUpdate = null;
        this.onPause = null;
        this.onResume = null;
        this.onGameOver = null;
        this.onVictory = null;
        this.onLevelComplete = null;
        
        this.lastTime = performance.now();
    }

    // Inicia el bucle del joc
    start() {
        this.gameScene.startRenderLoop(() => {
            if (!this.isPaused && !this.isGameOver && !this.isVictory) {
                const currentTime = performance.now();
                const deltaTime = (currentTime - this.lastTime) / 1000; // Convertir a segons
                this.lastTime = currentTime;

                // Cridar callback d'actualització
                if (this.onUpdate) {
                    this.onUpdate(deltaTime);
                }
            }
        });
    }

    // Pausa el joc
    pause() {
        if (!this.isPaused && !this.isGameOver && !this.isVictory) {
            this.isPaused = true;
            this.gameScene.exitPointerLock();
            if (this.onPause) {
                this.onPause();
            }
        }
    }

    // Reprèn el joc
    resume() {
        if (this.isPaused) {
            this.isPaused = false;
            this.lastTime = performance.now();
            this.gameScene.requestPointerLock();
            if (this.onResume) {
                this.onResume();
            }
        }
    }

    // Estableix Game Over
    setGameOver() {
        if (!this.isGameOver) {
            this.isGameOver = true;
            this.gameScene.exitPointerLock();
            if (this.onGameOver) {
                this.onGameOver();
            }
        }
    }

    // Estableix Victòria
    setVictory() {
        if (!this.isVictory) {
            this.isVictory = true;
            this.gameScene.exitPointerLock();
            if (this.onVictory) {
                this.onVictory();
            }
        }
    }

    // Passa al següent nivell
    nextLevel() {
        this.currentLevel++;
        if (this.currentLevel > this.maxLevels) {
            this.setVictory();
        } else if (this.onLevelComplete) {
            this.onLevelComplete(this.currentLevel);
        }
    }

    // Reinicia el joc
    reset() {
        this.isPaused = false;
        this.isGameOver = false;
        this.isVictory = false;
        this.currentLevel = 1;
        this.lastTime = performance.now();
    }

    // Comprova si el joc està en execució
    isRunning() {
        return !this.isPaused && !this.isGameOver && !this.isVictory;
    }
}
