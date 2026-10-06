# Battleship2 – Ficha 2 de Engenharia de Software

| Curso  | Número | Nome          | GitHub    |
|--------|--------|---------------|-----------|
| LEI-PL | 87907  | Pedro Vicente | LEI-87907 |

- **Vídeo de demonstração:** *(link do YouTube a acrescentar)*
- **User story implementada:** [#1 Exportar o histórico de rajadas para PDF](https://github.com/LEI-87907/Battleship2/issues/1)
- **Documentação (GitHub Pages):** https://lei-87907.github.io/Battleship2/

---

# ⚓ Battleship 2.0

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java Version](https://img.shields.io/badge/Java-17%2B-blue)
![License](https://img.shields.io/badge/license-MIT-green)

> A modern take on the classic naval warfare game, designed for the XVII century setting with updated software engineering patterns.

---

## 📖 Table of Contents
- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [Technical Stack](#-technical-stack)
- [Installation & Setup](#-installation--setup)
- [Code Architecture](#-code-architecture)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)

---

## 🎯 Project Overview
This project serves as a template and reference for students learning **Object-Oriented Programming (OOP)** and **Software Quality**. It simulates a battleship environment where players must strategically place ships and sink the enemy fleet.

### 🎮 The Rules
The game is played on a grid (typically 10x10). The coordinate system is defined as:

$$(x, y) \in \{0, \dots, 9\} \times \{0, \dots, 9\}$$

Hits are calculated based on the intersection of the shot vector and the ship's bounding box.

---

## ✨ Key Features
| Feature | Description | Status |
| :--- | :--- | :---: |
| **Grid System** | Flexible $N \times N$ board generation. | ✅ |
| **Ship Varieties** | Galleons, Frigates, and Brigantines (XVII Century theme). | ✅ |
| **AI Opponent** | Heuristic-based targeting system. | 🚧 |
| **Network Play** | Socket-based multiplayer. | ❌ |

---

## 🛠 Technical Stack
* **Language:** Java 17
* **Build Tool:** Maven / Gradle
* **Testing:** JUnit 5
* **Logging:** Log4j2

---

## 🚀 Installation & Setup

### Prerequisites
* JDK 17 or higher
* Git

### Step-by-Step
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/britoeabreu/Battleship2.git](https://github.com/britoeabreu/Battleship2.git)
   ```
2. **Navigate to directory:**
   ```bash
   cd Battleship2
   ```
3. **Compile and Run:**
   ```bash
   javac Main.java && java Main
   ```

---

## 📚 Documentation

You can access the generated Javadoc here:

👉 [Battleship2 API Documentation](https://britoeabreu.github.io/Battleship2/)


### Core Logic
```java
public class Ship {
    private String name;
    private int size;
    private boolean isSunk;

    // TODO: Implement damage logic
    public void hit() {
        // Implementation here
    }
}
```

### Design Patterns Used:
- **Strategy Pattern:** For different AI difficulty levels.
- **Observer Pattern:** To update the UI when a ship is hit.
</details>

### Logic Flow
```mermaid
graph TD
    A[Start Game] --> B{Place Ships}
    B --> C[Player Turn]
    C --> D[Target Coordinate]
    D --> E{Hit or Miss?}
    E -- Hit --> F[Check if Sunk]
    E -- Miss --> G[AI Turn]
    F --> G
    G --> C
```

---

## 🗺 Roadmap
- [x] Basic grid implementation
- [x] Ship placement validation
- [ ] Add sound effects (SFX)
- [ ] Implement "Fog of War" mechanic
- [ ] **Multiplayer Integration** (High Priority)

---

## 🧪 Testing
We use high-coverage unit testing to ensure game stability. Run tests using:
```bash
mvn test
```

> [!TIP]
> Use the `-Dtest=ClassName` flag to run specific test suites during development.

---

## 🤝 Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a **Pull Request**

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.


---
**Maintained by:** [@britoeabreu](https://github.com/britoeabreu)  
*Created for the Software Engineering students at ISCTE-IUL.*


---

## Treino do oponente de IA - Protocolo de comunicação (Parte C)

**LLM utilizado:** Claude (Anthropic)

1. Expliquei as regras da Batalha Naval dos Descobrimentos com o prompt da secção C2 do guião.
2. Pedi 4 tabuleiros, com o Galeão virado a Norte, Sul, Este e Oeste. Os 4 respeitaram as regras: 11 navios, sem contacto entre navios e Galeão em forma de T.
3. Ensinei o protocolo JSON com *few-shot prompting*. 
4. Joguei várias rajadas: introduzi cada rajada do LLM no programa e devolvi o JSON gerado.
5. O LLM confirmou que domina o protocolo.


<details>
<summary><b>Prompts utilizados na Parte C</b> (clicar para expandir)</summary>

**1. Regras do jogo (C2):** prompt do guião, sem alterações.

**2. Verificação dos tabuleiros (C3):**
```
Mostre-me um tabuleiro criado com estas regras, com o Galeão virado a Norte. Deixa as posições da água em branco para melhor visualização das posições dos navios.
Mostre-me um tabuleiro criado com estas regras, com o Galeão virado a Sul.
Mostre-me um tabuleiro criado com estas regras, com o Galeão virado a Este.
Mostre-me um tabuleiro criado com estas regras, com o Galeão virado a Oeste.
```

**3. Protocolo JSON (C4), com o formato da rajada corrigido para array:**
```
A nossa interação será através de objetos JSON, quer para a rajada de 3 tiros, quer para a correspondente resposta. Uma rajada tem o seguinte formato (um array com 3 tiros):
[ {"row": "A", "column": 5}, {"row": "C", "column": 10}, {"row": "F", "column": 5} ]
A resposta a uma rajada é feita em conjunto e não tiro a tiro 
Manda agora várias rajadas de tiros (uma de cada vez) e eu devolvo o JSON da resposta. Pare só quando tiver aprendido o protocolo de comunicação do jogo.
```

</details>

