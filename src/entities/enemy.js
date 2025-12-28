// src/entities/enemy.js
// Enemic amb IA bàsica, patrulla i atac

export class Enemy {
    constructor(scene, position, shadowGenerator) {
        this.scene = scene;
        this.position = position.clone();
        this.shadowGenerator = shadowGenerator;
        
        // Estadístiques
        this.maxHealth = 50;
        this.health = this.maxHealth;
        this.isAlive = true;
        this.damage = 10;
        this.attackRange = 3;
        this.detectionRange = 15;
        this.attackCooldown = 2;
        this.attackTimer = 0;
        
        // Moviment i patrulla
        this.moveSpeed = 2;
        this.patrolPoints = [];
        this.currentPatrolIndex = 0;
        this.isChasing = false;
        
        // Estat
        this.state = 'patrol'; // patrol, chase, attack
        
        // Mesh
        this.mesh = null;
        this.material = null;
        
        // Target (jugador)
        this.target = null;
        
        // Callbacks
        this.onDeath = null;
        this.onAttack = null;
        
        this._createMesh();
        this._setupPatrolPoints();
    }

    // Crea el mesh de l'enemic (capsa vermella)
    _createMesh() {
        // Crear capsa com a enemic
        this.mesh = BABYLON.MeshBuilder.CreateBox(
            "enemy",
            { width: 1, height: 1.8, depth: 1 },
            this.scene
        );
        this.mesh.position = this.position.clone();
        
        // Material vermell
        this.material = new BABYLON.StandardMaterial("enemyMat", this.scene);
        this.material.diffuseColor = new BABYLON.Color3(0.8, 0, 0);
        this.material.emissiveColor = new BABYLON.Color3(0.2, 0, 0);
        this.mesh.material = this.material;
        
        // Afegir a generador d'ombres
        if (this.shadowGenerator) {
            this.shadowGenerator.addShadowCaster(this.mesh);
        }
        
        // Marcar com a enemic
        this.mesh.isEnemy = true;
        this.mesh.enemyInstance = this;
        
        // Col·lisions
        this.mesh.checkCollisions = true;
    }

    // Configura punts de patrulla al voltant de la posició inicial
    _setupPatrolPoints() {
        const radius = 5;
        this.patrolPoints = [
            this.position.clone(),
            this.position.clone().add(new BABYLON.Vector3(radius, 0, 0)),
            this.position.clone().add(new BABYLON.Vector3(radius, 0, radius)),
            this.position.clone().add(new BABYLON.Vector3(0, 0, radius)),
            this.position.clone().add(new BABYLON.Vector3(-radius, 0, radius)),
            this.position.clone().add(new BABYLON.Vector3(-radius, 0, 0)),
        ];
        this.currentPatrolIndex = 0;
    }

    // Estableix el target (jugador)
    setTarget(target) {
        this.target = target;
    }

    // Actualitza l'enemic cada frame
    update(deltaTime) {
        if (!this.isAlive) return;

        // Actualitzar cooldown d'atac
        if (this.attackTimer > 0) {
            this.attackTimer -= deltaTime;
        }

        // Comprovar distància al jugador
        if (this.target && this.target.isAlive) {
            const distanceToTarget = BABYLON.Vector3.Distance(
                this.mesh.position,
                this.target.position
            );

            // Actualitzar estat segons distància
            if (distanceToTarget <= this.attackRange) {
                this.state = 'attack';
                this._attack();
            } else if (distanceToTarget <= this.detectionRange) {
                this.state = 'chase';
                this.isChasing = true;
                this._chaseTarget(deltaTime);
            } else {
                this.state = 'patrol';
                this.isChasing = false;
                this._patrol(deltaTime);
            }
        } else {
            this.state = 'patrol';
            this._patrol(deltaTime);
        }

        // Actualitzar posició
        this.position = this.mesh.position.clone();
    }

    // Patrulla entre punts
    _patrol(deltaTime) {
        const targetPoint = this.patrolPoints[this.currentPatrolIndex];
        const distance = BABYLON.Vector3.Distance(this.mesh.position, targetPoint);

        if (distance < 0.5) {
            // Arribar al punt, canviar al següent
            this.currentPatrolIndex = (this.currentPatrolIndex + 1) % this.patrolPoints.length;
        } else {
            // Moure cap al punt
            const direction = targetPoint.subtract(this.mesh.position).normalize();
            this.mesh.position.addInPlace(direction.scale(this.moveSpeed * deltaTime));
            
            // Rotar cap a la direcció
            this._lookAt(targetPoint);
        }
    }

    // Persegueix el target
    _chaseTarget(deltaTime) {
        if (!this.target) return;

        const direction = this.target.position.subtract(this.mesh.position).normalize();
        this.mesh.position.addInPlace(direction.scale(this.moveSpeed * 1.5 * deltaTime));
        
        // Rotar cap al target
        this._lookAt(this.target.position);
    }

    // Ataca el target
    _attack() {
        if (this.attackTimer > 0 || !this.target) return;

        this.attackTimer = this.attackCooldown;
        
        // Aplicar dany al jugador
        this.target.takeDamage(this.damage);
        
        // Callback d'atac
        if (this.onAttack) {
            this.onAttack(this.target);
        }

        // Efecte visual d'atac
        this._flashRed();
    }

    // Efecte visual quan ataca
    _flashRed() {
        this.material.emissiveColor = new BABYLON.Color3(1, 0, 0);
        setTimeout(() => {
            if (this.isAlive) {
                this.material.emissiveColor = new BABYLON.Color3(0.2, 0, 0);
            }
        }, 200);
    }

    // Rota el mesh per mirar cap a un punt
    _lookAt(targetPosition) {
        const direction = targetPosition.subtract(this.mesh.position);
        direction.y = 0;
        const angle = Math.atan2(direction.x, direction.z);
        this.mesh.rotation.y = angle;
    }

    // Rep dany
    takeDamage(amount) {
        if (!this.isAlive) return;

        this.health -= amount;
        
        // Efecte visual de dany
        this.material.emissiveColor = new BABYLON.Color3(1, 1, 0);
        setTimeout(() => {
            if (this.isAlive) {
                this.material.emissiveColor = new BABYLON.Color3(0.2, 0, 0);
            }
        }, 100);

        if (this.health <= 0) {
            this.health = 0;
            this._die();
        }
    }

    // Mor l'enemic
    _die() {
        this.isAlive = false;
        
        // Animació de mort (caure)
        const animation = new BABYLON.Animation(
            "deathAnim",
            "rotation.x",
            30,
            BABYLON.Animation.ANIMATIONTYPE_FLOAT,
            BABYLON.Animation.ANIMATIONLOOPMODE_CONSTANT
        );
        
        const keys = [
            { frame: 0, value: 0 },
            { frame: 30, value: Math.PI / 2 }
        ];
        animation.setKeys(keys);
        
        this.mesh.animations = [animation];
        this.scene.beginAnimation(this.mesh, 0, 30, false, 1, () => {
            // Després de l'animació, destruir el mesh
            setTimeout(() => {
                if (this.mesh) {
                    this.mesh.dispose();
                    this.mesh = null;
                }
            }, 1000);
        });
        
        // Callback de mort
        if (this.onDeath) {
            this.onDeath(this);
        }
    }

    // Neteja recursos
    dispose() {
        if (this.mesh) {
            this.mesh.dispose();
            this.mesh = null;
        }
        if (this.material) {
            this.material.dispose();
            this.material = null;
        }
    }
}
