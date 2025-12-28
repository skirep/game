// src/entities/player.js
// Jugador amb moviment, vida i capacitat de disparar

export class Player {
    constructor(scene, camera, position = new BABYLON.Vector3(0, 1.6, 0)) {
        this.scene = scene;
        this.camera = camera;
        this.position = position.clone();
        
        // Estadístiques del jugador
        this.maxHealth = 100;
        this.health = this.maxHealth;
        this.isAlive = true;
        
        // Moviment
        this.moveSpeed = 5;
        this.rotationSpeed = 2;
        this.moveDirection = BABYLON.Vector3.Zero();
        
        // Disparo
        this.canShoot = true;
        this.shootCooldown = 0.3; // segons
        this.shootTimer = 0;
        this.shootRange = 50;
        this.shootDamage = 25;
        
        // Callbacks
        this.onDeath = null;
        this.onShoot = null;
        this.onHit = null;
        
        // Actualitzar posició de la càmera
        this.camera.position = this.position.clone();
    }

    // Actualitza el jugador cada frame
    update(deltaTime) {
        if (!this.isAlive) return;

        // Actualitzar cooldown de disparo
        if (this.shootTimer > 0) {
            this.shootTimer -= deltaTime;
            if (this.shootTimer <= 0) {
                this.canShoot = true;
            }
        }

        // Actualitzar posició basada en moviment
        if (this.moveDirection.lengthSquared() > 0) {
            const movement = this.moveDirection.normalize().scale(this.moveSpeed * deltaTime);
            
            // Obtenir direcció de la càmera
            const forward = this.camera.getDirection(BABYLON.Axis.Z);
            const right = this.camera.getDirection(BABYLON.Axis.X);
            
            // Moure en el pla XZ (sense component Y)
            forward.y = 0;
            right.y = 0;
            forward.normalize();
            right.normalize();
            
            // Calcular moviment final
            const moveVector = forward.scale(movement.z).add(right.scale(movement.x));
            
            // Aplicar moviment amb col·lisions
            this.camera.position.addInPlace(moveVector);
        }
        
        // Actualitzar posició del jugador
        this.position = this.camera.position.clone();
    }

    // Estableix direcció de moviment
    setMoveDirection(x, z) {
        this.moveDirection.x = x;
        this.moveDirection.z = z;
    }

    // Rota la càmera
    rotate(yaw, pitch) {
        this.camera.rotation.y += yaw * this.rotationSpeed;
        this.camera.rotation.x += pitch * this.rotationSpeed;
        
        // Limitar rotació vertical
        this.camera.rotation.x = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, this.camera.rotation.x));
    }

    // Dispara un raig
    shoot() {
        if (!this.canShoot || !this.isAlive) return null;

        this.canShoot = false;
        this.shootTimer = this.shootCooldown;

        // Crear raig des de la càmera
        const ray = this.camera.getForwardRay(this.shootRange);
        
        // Detectar col·lisió amb enemies
        const hit = this.scene.pickWithRay(ray, (mesh) => {
            return mesh.isEnemy === true;
        });

        // Callback de disparo
        if (this.onShoot) {
            this.onShoot(ray, hit);
        }

        return hit;
    }

    // Rep dany
    takeDamage(amount) {
        if (!this.isAlive) return;

        this.health -= amount;
        
        if (this.health <= 0) {
            this.health = 0;
            this.isAlive = false;
            if (this.onDeath) {
                this.onDeath();
            }
        }

        // Callback quan rep dany
        if (this.onHit) {
            this.onHit(amount, this.health);
        }
    }

    // Cura el jugador
    heal(amount) {
        this.health = Math.min(this.health + amount, this.maxHealth);
    }

    // Reinicia el jugador
    reset(position = null) {
        this.health = this.maxHealth;
        this.isAlive = true;
        this.canShoot = true;
        this.shootTimer = 0;
        this.moveDirection = BABYLON.Vector3.Zero();
        
        if (position) {
            this.position = position.clone();
            this.camera.position = position.clone();
        }
    }

    // Obté el percentatge de vida
    getHealthPercent() {
        return (this.health / this.maxHealth) * 100;
    }
}
