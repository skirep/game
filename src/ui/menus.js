// src/ui/menus.js
// Gestió de menús i pantalles

export class MenuManager {
    constructor() {
        // Menú principal
        this.mainMenu = document.getElementById('mainMenu');
        this.startButton = document.getElementById('startButton');
        
        // Menú de pausa
        this.pauseMenu = document.getElementById('pauseMenu');
        this.resumeButton = document.getElementById('resumeButton');
        this.quitButton = document.getElementById('quitButton');
        
        // Game Over
        this.gameOverScreen = document.getElementById('gameOverScreen');
        this.restartButton = document.getElementById('restartButton');
        
        // Victòria
        this.victoryScreen = document.getElementById('victoryScreen');
        this.playAgainButton = document.getElementById('playAgainButton');
        
        // Canvas
        this.canvas = document.getElementById('renderCanvas');
        
        // Callbacks
        this.onStartGame = null;
        this.onResumeGame = null;
        this.onQuitToMenu = null;
        this.onRestartGame = null;
        
        this._setupListeners();
    }

    // Configura els event listeners
    _setupListeners() {
        this.startButton.addEventListener('click', () => {
            if (this.onStartGame) {
                this.onStartGame();
            }
        });

        this.resumeButton.addEventListener('click', () => {
            if (this.onResumeGame) {
                this.onResumeGame();
            }
        });

        this.quitButton.addEventListener('click', () => {
            if (this.onQuitToMenu) {
                this.onQuitToMenu();
            }
        });

        this.restartButton.addEventListener('click', () => {
            if (this.onRestartGame) {
                this.onRestartGame();
            }
        });

        this.playAgainButton.addEventListener('click', () => {
            if (this.onRestartGame) {
                this.onRestartGame();
            }
        });
    }

    // Mostra el menú principal
    showMainMenu() {
        this.mainMenu.style.display = 'flex';
        this.pauseMenu.style.display = 'none';
        this.gameOverScreen.style.display = 'none';
        this.victoryScreen.style.display = 'none';
        this.canvas.style.display = 'none';
    }

    // Amaga tots els menús (joc actiu)
    hideAllMenus() {
        this.mainMenu.style.display = 'none';
        this.pauseMenu.style.display = 'none';
        this.gameOverScreen.style.display = 'none';
        this.victoryScreen.style.display = 'none';
        this.canvas.style.display = 'block';
    }

    // Mostra el menú de pausa
    showPauseMenu() {
        this.pauseMenu.style.display = 'flex';
    }

    // Amaga el menú de pausa
    hidePauseMenu() {
        this.pauseMenu.style.display = 'none';
    }

    // Mostra la pantalla de Game Over
    showGameOver() {
        this.gameOverScreen.style.display = 'flex';
    }

    // Mostra la pantalla de victòria
    showVictory() {
        this.victoryScreen.style.display = 'flex';
    }
}
