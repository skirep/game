// src/levels/level-builder.js
// Constructor de nivells a partir de mapes basats en graella

export class LevelBuilder {
    constructor(scene, shadowGenerator) {
        this.scene = scene;
        this.shadowGenerator = shadowGenerator;
        this.wallHeight = 3;
        this.cellSize = 4;
    }

    // Construeix un nivell a partir d'un mapa
    buildLevel(levelData) {
        const { map, playerSpawn } = levelData;
        const meshes = [];

        // Crear terra
        const ground = this._createGround(map[0].length, map.length);
        meshes.push(ground);

        // Crear sostre
        const ceiling = this._createCeiling(map[0].length, map.length);
        meshes.push(ceiling);

        // Crear murs a partir del mapa
        for (let z = 0; z < map.length; z++) {
            for (let x = 0; x < map[z].length; x++) {
                if (map[z][x] === 1) {
                    const wall = this._createWall(x, z);
                    meshes.push(wall);
                }
            }
        }

        return {
            meshes,
            playerSpawn: new BABYLON.Vector3(
                playerSpawn.x * this.cellSize,
                1.6,
                playerSpawn.z * this.cellSize
            )
        };
    }

    // Crea el terra
    _createGround(width, depth) {
        const ground = BABYLON.MeshBuilder.CreateGround(
            "ground",
            { width: width * this.cellSize, height: depth * this.cellSize },
            this.scene
        );

        const groundMat = new BABYLON.StandardMaterial("groundMat", this.scene);
        groundMat.diffuseColor = new BABYLON.Color3(0.3, 0.3, 0.3);
        groundMat.specularColor = new BABYLON.Color3(0, 0, 0);
        ground.material = groundMat;
        ground.position.y = 0;
        ground.position.x = (width * this.cellSize) / 2 - this.cellSize / 2;
        ground.position.z = (depth * this.cellSize) / 2 - this.cellSize / 2;
        ground.receiveShadows = true;
        ground.checkCollisions = true;

        return ground;
    }

    // Crea el sostre
    _createCeiling(width, depth) {
        const ceiling = BABYLON.MeshBuilder.CreateGround(
            "ceiling",
            { width: width * this.cellSize, height: depth * this.cellSize },
            this.scene
        );

        const ceilingMat = new BABYLON.StandardMaterial("ceilingMat", this.scene);
        ceilingMat.diffuseColor = new BABYLON.Color3(0.2, 0.2, 0.2);
        ceilingMat.specularColor = new BABYLON.Color3(0, 0, 0);
        ceiling.material = ceilingMat;
        ceiling.position.y = this.wallHeight;
        ceiling.position.x = (width * this.cellSize) / 2 - this.cellSize / 2;
        ceiling.position.z = (depth * this.cellSize) / 2 - this.cellSize / 2;
        ceiling.rotation.z = Math.PI;

        return ceiling;
    }

    // Crea un mur
    _createWall(x, z) {
        const wall = BABYLON.MeshBuilder.CreateBox(
            `wall_${x}_${z}`,
            { width: this.cellSize, height: this.wallHeight, depth: this.cellSize },
            this.scene
        );

        const wallMat = new BABYLON.StandardMaterial(`wallMat_${x}_${z}`, this.scene);
        wallMat.diffuseColor = new BABYLON.Color3(0.5, 0.5, 0.6);
        wallMat.specularColor = new BABYLON.Color3(0.2, 0.2, 0.2);
        wall.material = wallMat;

        wall.position.x = x * this.cellSize;
        wall.position.y = this.wallHeight / 2;
        wall.position.z = z * this.cellSize;

        wall.checkCollisions = true;

        if (this.shadowGenerator) {
            this.shadowGenerator.addShadowCaster(wall);
        }

        return wall;
    }

    // Neteja tots els meshes del nivell
    cleanLevel(meshes) {
        meshes.forEach(mesh => {
            if (mesh) {
                mesh.dispose();
            }
        });
    }
}
