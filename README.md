# Inversion de Contrôle et Injection de Dépendances

## Présentation

Ce projet est un exemple complet pour comprendre l'**inversion de contrôle (IoC)** et l'**injection de dépendances** en Java, avec quatre approches différentes : statique, dynamique par réflexion, Spring XML, et Spring annotations.

L'idée principale : au lieu qu'une classe crée elle-même les objets dont elle a besoin (avec `new`), on lui **donne** ces objets depuis l'extérieur. Cela rend le code plus flexible et plus facile à modifier.

## Structure du projet

```
src/main/java/
├── dao/
│   ├── IDao.java              → interface de la couche accès aux données
│   ├── DaoImpl.java            → implémentation qui simule une base de données
│   └── DaoImplV2.java          → implémentation qui simule un web service
├── metier/
│   ├── IMetier.java            → interface de la couche métier
│   └── MetierImpl.java         → contient la logique de calcul
└── pres/
    ├── Presntation1.java        → injection statique (sans Spring)
    ├── Presntation2.java        → injection dynamique par réflexion (sans Spring)
    ├── PresSpringXml.java       → injection Spring via fichier XML
    └── PresSpringAnnotation.java → injection Spring via annotations

src/main/resources/
└── applicationContext.xml    → configuration des beans pour la version Spring XML

config.txt   → fichier utilisé par Presntation2 (réflexion)
```

## Les trois couches du projet

1. **Couche DAO** (`dao`) : gère l'accès aux données. L'interface `IDao` définit une méthode `getData()`. Deux classes l'implémentent différemment : `DaoImpl` (simule une base de données) et `DaoImplV2` (simule un web service).

2. **Couche Métier** (`metier`) : contient la logique de calcul. La classe `MetierImpl` utilise l'interface `IDao`, mais **ne connaît pas** quelle implémentation exacte elle utilise. C'est ça, le couplage faible.

3. **Couche Présentation** (`pres`) : c'est ici que tout démarre, avec quatre variantes possibles selon la méthode d'injection choisie.

## Les quatre méthodes d'injection

### 1. Injection statique — `Presntation1.java`

On crée les objets directement dans le code avec `new` :

```java
DaoImpl d = new DaoImpl();
MetierImpl metier = new MetierImpl(d);
```

**Inconvénient** : changer d'implémentation nécessite de modifier le code et de recompiler.

### 2. Injection dynamique par réflexion — `Presntation2.java`

On lit le nom des classes à utiliser dans un fichier externe `config.txt`, puis on utilise la réflexion Java (`Class.forName`) pour créer les objets sans les connaître à l'avance.

**Avantage** : changer d'implémentation se fait juste en modifiant `config.txt`, sans recompiler.

### 3. Injection avec Spring XML — `PresSpringXml.java`

On délègue la création et l'injection des objets à Spring, via un fichier `applicationContext.xml` :

```xml
<bean id="dao" class="dao.DaoImpl"></bean>

<bean id="metier" class="metier.MetierImpl">
    <constructor-arg ref="dao"></constructor-arg>
</bean>
```

```java
ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
IMetier metier = context.getBean(IMetier.class);
```

**Avantage** : plus besoin d'écrire soi-même le code de réflexion. Spring s'occupe de tout à partir du XML.

### 4. Injection avec Spring annotations — `PresSpringAnnotation.java`

On utilise des annotations directement dans le code Java, sans fichier XML :

```java
@Component("daoImplV2")
public class DaoImplV2 implements IDao { ... }

@Service("metier")
public class MetierImpl implements IMetier {

    private IDao dao;

    @Autowired
    public MetierImpl(@Qualifier("daoImplV2") IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double t = dao.getData();
        return t * 43 / 3;
    }
}
```

```java
ApplicationContext context = new AnnotationConfigApplicationContext("metier", "dao");
IMetier metier = (IMetier) context.getBean("metier");
```

**Avantage** : configuration plus rapide à écrire, directement dans les classes. C'est l'approche la plus utilisée aujourd'hui.

## Annotations Spring utilisées

| Annotation | Rôle |
|---|---|
| `@Component` | Déclare une classe comme un bean générique géré par Spring |
| `@Service` | Spécialisation de `@Component` pour la couche métier (sémantiquement plus précise) |
| `@Autowired` | Demande à Spring d'injecter automatiquement une dépendance |
| `@Qualifier` | Précise quel bean injecter quand plusieurs implémentations existent (ex: `DaoImpl` vs `DaoImplV2`) |

## Pourquoi `@Qualifier` est nécessaire ici

Le projet contient **deux implémentations** de `IDao` (`DaoImpl` et `DaoImplV2`). Sans précision, Spring ne sait pas laquelle injecter et lève une erreur d'ambiguïté. `@Qualifier("daoImplV2")` permet de dire explicitement à Spring quel bean utiliser.

## Résultat d'exécution (`PresSpringAnnotation`)

```
Version web service
Resultat : 630.6666666666666
```

Ce résultat confirme que `DaoImplV2` (`getData()` retourne `41`) a bien été injecté dans `MetierImpl`, puis utilisé dans le calcul : `41 * 43 / 3 ≈ 630.67`.

## Comment exécuter le projet
 

### Avec IntelliJ

Ouvre le projet, clique droit sur la classe voulue, puis choisis **Run**.

## Dépendances Maven

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>6.2.3</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.2.3</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>6.2.3</version>
</dependency>
```

## Pourquoi c'est important ?

Ce principe est la base du fonctionnement de **Spring Framework**. Comprendre l'injection manuelle et par réflexion permet de mieux comprendre ce qui se passe **en interne** quand on utilise Spring — que ce soit avec XML ou avec des annotations.

## Lien avec le principe Ouvert/Fermé (SOLID)

Ce projet illustre aussi le principe **Ouvert/Fermé** (*Open/Closed Principle*, le "O" de SOLID) : **une classe doit être ouverte à l'extension, mais fermée à la modification**.

La classe `MetierImpl` ne dépend que de l'interface `IDao`, jamais d'une implémentation concrète. Résultat : elle reste **fermée à la modification** (on ne la change jamais), tout en étant **ouverte à l'extension** (on peut ajouter de nouvelles implémentations de `IDao` sans y toucher).

| Action | Fichier modifié | Fichier non modifié |
|---|---|---|
| Passer de `DaoImpl` à `DaoImplV2` | `config.txt`, `applicationContext.xml` ou `@Qualifier` | `MetierImpl.java` |
| Ajouter une 3ᵉ implémentation (ex: `DaoImplV3`) | Une nouvelle classe `DaoImplV3` | `MetierImpl.java` |

L'injection de dépendances est le mécanisme qui **rend ce principe possible en pratique** :
- **Sans injection** : `MetierImpl` ferait `new DaoImpl()` directement → couplage fort → impossible de respecter l'Ouvert/Fermé, car changer d'implémentation obligerait à modifier `MetierImpl`.
- **Avec injection** : `MetierImpl` dépend seulement de l'interface `IDao` → couplage faible → le principe est respecté.

> L'injection de dépendances permet de respecter le principe Ouvert/Fermé, car elle découple la couche métier des implémentations concrètes de la couche DAO. On peut ainsi ajouter de nouvelles sources de données sans jamais modifier le code métier existant, ce qui réduit les risques de régression.

## Auteur

Projet réalisé dans le cadre du module **Architecture JEE et Systèmes Distribués**