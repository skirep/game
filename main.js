// main.js
// Fitxer principal que integra tots els components del joc

import { GameScene } from './src/engine/scene.js';
import { GameLoop } from './src/engine/game-loop.js';
import { Player } from './src/entities/player.js';
import { Enemy } from './src/entities/enemy.js';
import { LevelBuilder } from './src/levels/level-builder.js';
import { LEVELS } from './src/levels/levels.js';
import { KeyboardControls } from './src/controls/keyboard.js';
import { MouseControls } from './src/controls/mouse.js';
import { TouchControls } from './src/controls/touch.js';
import { HUD } from './src/ui/hud.js';
import { MenuManager } from './src/ui/menus.js';

class Game {
    constructor() {
        // Components principals
        this.gameScene = null;
        this.gameLoop = null;
        this.player = null;
        this.enemies = [];
        this.levelBuilder = null;
        this.currentLevelMeshes = [];
        
        // Controls
        this.keyboard = null;
        this.mouse = null;
        this.touch = null;
        this.isMobile = TouchControls.isMobile();
        
        // UI
        this.hud = null;
        this.menuManager = null;
        
        // Estat
        this.isInitialized = false;
        this.pauseKeyPressed = false;
    }

    // Inicialitza el joc
    async init() {
        console.log('Inicialitzant el joc...');
        
        // Crear escena
        this.gameScene = new GameScene();
        this.gameScene.init();
        
        // Crear bucle del joc
        this.gameLoop = new GameLoop(this.gameScene);
        
        // Crear level builder
        this.levelBuilder = new LevelBuilder(
            this.gameScene.scene,
            this.gameScene.shadowGenerator
        );
        
        // Crear controls
        this.keyboard = new KeyboardControls();
        this.mouse = new MouseControls(this.gameScene.canvas);
        this.touch = new TouchControls();
        
        // Crear UI
        this.hud = new HUD();
        this.menuManager = new MenuManager();
        
        // Configurar callbacks dels menús
        this._setupMenuCallbacks();
        
        // Configurar callbacks del bucle
        this._setupGameLoopCallbacks();
        
        // Mostrar menú principal
        this.menuManager.showMainMenu();
        
        this.isInitialized = true;
        console.log('Joc inicialitzat correctament');
    }

    // Configura els callbacks dels menús
    _setupMenuCallbacks() {
        this.menuManager.onStartGame = () => this.startGame();
        this.menuManager.onResumeGame = () => this.resumeGame();
        this.menuManager.onQuitToMenu = () => this.quitToMenu();
        this.menuManager.onRestartGame = () => this.restartGame();
    }

    // Configura els callbacks del bucle del joc
    _setupGameLoopCallbacks() {
        this.gameLoop.onUpdate = (deltaTime) => this.update(deltaTime);
        this.gameLoop.onPause = () => this.onPause();
        this.gameLoop.onResume = () => this.onResume();
        this.gameLoop.onGameOver = () => this.onGameOver();
        this.gameLoop.onVictory = () => this.onVictory();
        this.gameLoop.onLevelComplete = (level) => this.loadLevel(level);
    }

    // Inicia el joc
    startGame() {
        console.log('Iniciant partida...');
        
        // Amagar menús i mostrar canvas
        this.menuManager.hideAllMenus();
        this.hud.show();
        
        // Carregar primer nivell
        this.loadLevel(1);
        
        // Activar controls
        this._enableControls();
        
        // Demanar pointer lock (només ordinador)
        if (!this.isMobile) {
            this.gameScene.requestPointerLock();
        }
        
        // Iniciar bucle
        if (!this.gameLoop.isRunning()) {
            this.gameLoop.start();
        }
        
        console.log('Partida iniciada');
    }

    // Carrega un nivell
    loadLevel(levelNumber) {
        console.log(`Carregant nivell ${levelNumber}...`);
        
        // Netejar nivell anterior
        this._cleanupLevel();
        
        // Obtenir dades del nivell
        const levelData = LEVELS[levelNumber - 1];
        if (!levelData) {
            console.error('Nivell no trobat');
            return;
        }
        
        // Construir nivell
        const levelResult = this.levelBuilder.buildLevel(levelData);
        this.currentLevelMeshes = levelResult.meshes;
        
        // Crear jugador
        this.player = new Player(
            this.gameScene.scene,
            this.gameScene.camera,
            levelResult.playerSpawn
        );
        
        // Configurar callbacks del jugador
        this.player.onDeath = () => this.gameLoop.setGameOver();
        this.player.onHit = (damage, health) => {
            this.hud.updateHealth(health, this.player.maxHealth);
        };
        
        // Crear enemics
        this.enemies = [];
        levelData.enemySpawns.forEach(spawn => {
            const position = new BABYLON.Vector3(
                spawn.x * this.levelBuilder.cellSize,
                0.9,
                spawn.z * this.levelBuilder.cellSize
            );
            
            const enemy = new Enemy(
                this.gameScene.scene,
                position,
                this.gameScene.shadowGenerator
            );
            enemy.setTarget(this.player);
            enemy.onDeath = (deadEnemy) => this._onEnemyDeath(deadEnemy);
            
            this.enemies.push(enemy);
        });
        
        // Actualitzar HUD
        this.hud.updateLevel(levelNumber);
        this.hud.updateHealth(this.player.health, this.player.maxHealth);
        this.hud.updateEnemies(this.enemies.length);
        
        console.log(`Nivell ${levelNumber} carregat amb ${this.enemies.length} enemics`);
    }

    // Neteja el nivell actual
    _cleanupLevel() {
        // Netejar meshes del nivell
        if (this.currentLevelMeshes.length > 0) {
            this.levelBuilder.cleanLevel(this.currentLevelMeshes);
            this.currentLevelMeshes = [];
        }
        
        // Netejar enemics
        this.enemies.forEach(enemy => enemy.dispose());
        this.enemies = [];
        
        // Netejar jugador
        this.player = null;
    }

    // Actualització del joc (cada frame)
    update(deltaTime) {
        // Actualitzar jugador
        if (this.player) {
            // Moviment del teclat
            const keyboardMove = this.keyboard.getMoveDirection();
            this.player.setMoveDirection(keyboardMove.x, keyboardMove.z);
            
            this.player.update(deltaTime);
        }
        
        // Actualitzar enemics
        this.enemies.forEach(enemy => {
            if (enemy.isAlive) {
                enemy.update(deltaTime);
            }
        });
        
        // Comprovar tecla de pausa
        if (this.keyboard.isPausePressed()) {
            if (!this.pauseKeyPressed) {
                this.pauseKeyPressed = true;
                this.gameLoop.pause();
            }
        } else {
            this.pauseKeyPressed = false;
        }
        
        // Comprovar si tots els enemics han mort
        const aliveEnemies = this.enemies.filter(e => e.isAlive).length;
        if (aliveEnemies === 0 && this.enemies.length > 0) {
            // Nivell completat
            console.log('Nivell completat!');
            setTimeout(() => {
                this.gameLoop.nextLevel();
            }, 1000);
        }
    }

    // Activa els controls
    _enableControls() {
        // Teclat sempre actiu
        this.keyboard.enable();
        
        if (this.isMobile) {
            // Controls tàctils
            this.touch.enable();
            this.touch.onMove = (x, y) => {
                if (this.player) {
                    this.player.setMoveDirection(x, y);
                }
            };
            this.touch.onRotate = (yaw, pitch) => {
                if (this.player) {
                    this.player.rotate(yaw, pitch);
                }
            };
            this.touch.onShoot = () => {
                if (this.player) {
                    const hit = this.player.shoot();
                    if (hit && hit.pickedMesh && hit.pickedMesh.enemyInstance) {
                        hit.pickedMesh.enemyInstance.takeDamage(this.player.shootDamage);
                    }
                }
            };
            this.touch.onPause = () => {
                this.gameLoop.pause();
            };
        } else {
            // Controls de ratolí
            this.mouse.enable();
            this.mouse.onRotate = (yaw, pitch) => {
                if (this.player) {
                    this.player.rotate(yaw, pitch);
                }
            };
            this.mouse.onShoot = () => {
                if (this.player) {
                    const hit = this.player.shoot();
                    if (hit && hit.pickedMesh && hit.pickedMesh.enemyInstance) {
                        hit.pickedMesh.enemyInstance.takeDamage(this.player.shootDamage);
                    }
                }
            };
        }
    }

    // Desactiva els controls
    _disableControls() {
        this.keyboard.disable();
        this.mouse.disable();
        this.touch.disable();
    }

    // Quan un enemic mor
    _onEnemyDeath(enemy) {
        const aliveEnemies = this.enemies.filter(e => e.isAlive).length;
        this.hud.updateEnemies(aliveEnemies);
        console.log(`Enemic eliminat! Resten ${aliveEnemies} enemics`);
    }

    // Pausa el joc
    onPause() {
        console.log('Joc pausat');
        this.menuManager.showPauseMenu();
    }

    // Reprèn el joc
    resumeGame() {
        console.log('Reprenent joc...');
        this.menuManager.hidePauseMenu();
        this.gameLoop.resume();
    }

    // Torna al menú principal
    quitToMenu() {
        console.log('Tornant al menú...');
        this._disableControls();
        this._cleanupLevel();
        this.hud.hide();
        this.menuManager.showMainMenu();
        this.gameLoop.reset();
    }

    // Game Over
    onGameOver() {
        console.log('Game Over');
        this._disableControls();
        this.hud.hide();
        this.menuManager.showGameOver();
    }

    // Victòria
    onVictory() {
        console.log('Has guanyat!');
        this._disableControls();
        this.hud.hide();
        this.menuManager.showVictory();
    }

    // Reinicia el joc
    restartGame() {
        console.log('Reiniciant joc...');
        this.menuManager.hideAllMenus();
        this.gameLoop.reset();
        this.startGame();
    }
}

// Inicialitzar el joc quan es carrega la pàgina
window.addEventListener('DOMContentLoaded', async () => {
    console.log('Pàgina carregada, inicialitzant joc...');
    const game = new Game();
    await game.init();
});
