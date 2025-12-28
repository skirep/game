// src/ui/hud.js
// Gestió del HUD (Head-Up Display)

export class HUD {
    constructor() {
        this.hudElement = document.getElementById('hud');
        this.healthBar = document.getElementById('healthBar');
        this.healthText = document.getElementById('healthText');
        this.levelText = document.getElementById('levelText');
        this.enemiesText = document.getElementById('enemiesText');
    }

    // Mostra el HUD
    show() {
        this.hudElement.style.display = 'block';
    }

    // Amaga el HUD
    hide() {
        this.hudElement.style.display = 'none';
    }

    // Actualitza la vida del jugador
    updateHealth(health, maxHealth) {
        const percent = (health / maxHealth) * 100;
        this.healthBar.style.width = percent + '%';
        this.healthText.textContent = Math.round(health);
        
        // Canviar color segons la vida
        if (percent <= 25) {
            this.healthBar.style.background = 'linear-gradient(90deg, #ff0000 0%, #990000 100%)';
        } else if (percent <= 50) {
            this.healthBar.style.background = 'linear-gradient(90deg, #ff6600 0%, #ff3300 100%)';
        } else {
            this.healthBar.style.background = 'linear-gradient(90deg, #ff0000 0%, #ff6666 100%)';
        }
    }

    // Actualitza el nivell actual
    updateLevel(level) {
        this.levelText.textContent = level;
    }

    // Actualitza el nombre d'enemics restants
    updateEnemies(count) {
        this.enemiesText.textContent = count;
    }
}
