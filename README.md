# Wolfenstein 3D - Retro FPS

Un joc de tir en primera persona inspirat en el clàssic Wolfenstein 3D, desenvolupat amb Babylon.js i JavaScript pur.

## 🎮 Descripció

Aquest és un shooter retro en primera persona amb gràfics 3D simples. El joc inclou 3 nivells amb 10 enemics cadascun, amb mecàniques de combat, patrulla d'enemics i progressió de nivells.

### Característiques

- ✨ Gràfics 3D utilitzant Babylon.js
- 🎯 3 nivells diferents amb disseny únic
- 👾 10 enemics per nivell amb IA bàsica
- 🎮 Compatible amb ordinador i dispositius mòbils
- 📱 Controls tàctils per a mòbils (joystick virtual)
- 🏃 Moviment fluid i combat dinàmic
- 💯 Sistema de vida i progressió de nivells

## 🎯 Controls

### Ordinador

- **WASD** - Moure's
- **Ratolí** - Mirar al voltant
- **Click esquerre** - Disparar
- **ESC** - Pausa

### Mòbil / Tàctil

- **Joystick virtual** (esquerra) - Moure's
- **Gestos tàctils** - Mirar al voltant
- **Botó FIRE** (dreta) - Disparar
- **Botó Pausa** (dalt dreta) - Pausar el joc

## 🚀 Com executar-lo localment

### Opció 1: Servidor HTTP Simple amb Python

```bash
# Python 3
python -m http.server 8000

# Python 2
python -m SimpleHTTPServer 8000
```

Després obre el navegador i ves a: `http://localhost:8000`

### Opció 2: Node.js amb http-server

```bash
# Instal·lar http-server globalment
npm install -g http-server

# Executar al directori del projecte
http-server

# O especificant port
http-server -p 8000
```

### Opció 3: VS Code Live Server

1. Instal·la l'extensió "Live Server" a VS Code
2. Clica dret sobre `index.html`
3. Selecciona "Open with Live Server"

## 📦 Com publicar-lo a GitHub Pages

1. Puja tot el projecte al teu repositori GitHub
2. Ves a **Settings** del repositori
3. Navega a la secció **Pages** (al menú lateral)
4. A **Source**, selecciona:
   - Branch: `main` (o `master`)
   - Folder: `/ (root)`
5. Clica **Save**
6. Espera uns segons i refresca la pàgina
7. Veuràs l'URL del teu joc publicat!

L'URL serà: `https://<el-teu-usuari>.github.io/<nom-del-repo>/`

## 📁 Estructura del Projecte

```
/
├── index.html              # Pàgina principal HTML
├── main.js                 # Punt d'entrada del joc
├── style.css               # Estils CSS
├── README.md               # Aquest fitxer
├── src/
│   ├── engine/             # Motor del joc
│   │   ├── scene.js        # Escena Babylon.js
│   │   └── game-loop.js    # Bucle principal del joc
│   ├── entities/           # Entitats del joc
│   │   ├── player.js       # Jugador
│   │   └── enemy.js        # Enemics
│   ├── levels/             # Nivells
│   │   ├── level-builder.js # Constructor de nivells
│   │   └── levels.js       # Definició dels 3 nivells
│   ├── controls/           # Controls
│   │   ├── keyboard.js     # Controls de teclat
│   │   ├── mouse.js        # Controls de ratolí
│   │   └── touch.js        # Controls tàctils
│   └── ui/                 # Interfície d'usuari
│       ├── hud.js          # HUD (vida, nivell, enemics)
│       └── menus.js        # Menús (inici, pausa, game over)
└── assets/                 # Recursos (textures, etc.)
```

## 🛠️ Tecnologies Utilitzades

- **Babylon.js** - Motor 3D
- **JavaScript (ES6 Modules)** - Programació
- **HTML5** - Estructura
- **CSS3** - Estils i UI

## 🎯 Objectiu del Joc

1. Elimina tots els 10 enemics de cada nivell
2. Sobreviu als atacs enemics
3. Completa els 3 nivells per guanyar

## 🐛 Solució de Problemes

### El joc no carrega

- Assegura't que estàs executant el joc des d'un servidor HTTP (no obrint directament l'arxiu HTML)
- Comprova la consola del navegador per veure errors
- Verifica que tens connexió a internet (per carregar Babylon.js des del CDN)

### Controls tàctils no funcionen

- Assegura't que estàs utilitzant un dispositiu tàctil o el mode mòbil del navegador
- Comprova que tens activat JavaScript al navegador

### Rendiment lent

- Prova amb un navegador modern (Chrome, Firefox, Edge)
- Redueix la resolució de la finestra del navegador
- Tanca altres pestanyes del navegador

## 📝 Llicència

Aquest projecte és de codi obert i està disponible per a ús educatiu.

## 👨‍💻 Desenvolupament

Tot el codi està comentat en català per facilitar la comprensió i el manteniment.

### Requisits del Sistema

- Navegador modern amb suport per:
  - WebGL
  - ES6 Modules
  - Pointer Lock API (per ordinador)
  - Touch Events (per mòbils)

## 🎨 Crèdits

Desenvolupat com a projecte de demostració d'un joc 3D web utilitzant Babylon.js.

---

**Gaudeix del joc! 🎮**