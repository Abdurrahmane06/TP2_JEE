# REPONSES.md — TP02 IoC et injection de dépendances

---

## Question 1 — IoC vs DI : différence et rôle du conteneur Spring

**IoC (Inversion of Control)** est un *principe* : au lieu que chaque objet crée lui-même
ses dépendances avec `new`, c'est un acteur externe (le **conteneur Spring / ApplicationContext**)
qui prend en charge cette responsabilité.

**DI (Dependency Injection)** est le *mécanisme concret* qui réalise cet IoC : Spring *injecte*
les objets nécessaires dans les beans qui en ont besoin (par constructeur, setter ou champ).

**Comparaison :**

```java
// Sans IoC (couplage fort)
Coach myCoach = new CricketCoach(); // DemoController decide lui-meme de l'implementation

// Avec IoC / DI (couplage faible)
// Spring cree le bon bean et l'injecte via le constructeur
public DemoController(Coach theCoach) { this.myCoach = theCoach; }
```

Le conteneur « inverse le contrôle » : ce n'est plus `DemoController` qui fait
`new CricketCoach()`, c'est Spring qui choisit, crée et fournit l'implémentation.

---

## Question 2 — Pourquoi l'injection par constructeur est recommandée

| Argument | Explication |
|---|---|
| **Immuabilité** | Les dépendances peuvent être `final` — garantit qu'elles ne changent jamais après construction. |
| **Testabilité** | Un test unitaire peut instancier la classe directement avec un mock, sans contexte Spring. |
| **Objet toujours valide** | Impossible d'avoir un bean partiellement initialisé : les dépendances sont fournies ou la construction échoue. |
| **Détection précoce** | Spring détecte les cycles de dépendances circulaires dès le démarrage. |

L'injection par champ (`@Autowired` directement sur le champ) est concise mais rend la
classe non testable sans conteneur Spring, et empêche l'utilisation de `final`.

---

## Question 3 — `NoUniqueBeanDefinitionException`

**Quand** : Spring trouve plusieurs beans du même type lors d'une injection sans indication
précise du bean voulu. Exemple : quatre coachs (`cricketCoach`, `baseballCoach`, `tennisCoach`,
`trackCoach`) tous `@Component` → Spring ne sait lequel injecter dans `DemoController`.

**Deux résolutions :**

| Solution | Mécanisme | Priorité |
|---|---|---|
| `@Qualifier("cricketCoach")` | Désigne *explicitement* le bean par son id | **Prioritaire** |
| `@Primary` | Marque un bean comme choix *par défaut* | Moins prioritaire |

**Règle d'or** : si `@Qualifier` et `@Primary` sont tous deux présents, **`@Qualifier` gagne**.

---

## Question 4 — Id par défaut d'un bean / id forcé

**Id par défaut** : Spring prend le **nom de la classe avec la première lettre en minuscule**.

Exemples :
- `CricketCoach`  → `cricketCoach`
- `TrackCoach`    → `trackCoach`
- `SportConfig#swimCoach()` → `swimCoach` (nom de la méthode `@Bean`)

**Forcer un id :**
```java
@Component("monCoach")   // via @Component
@Bean("aquatic")         // via @Bean (ex. 5.8 : SwimCoach -> "aquatic")
```

---

## Question 5 — Avantages et inconvénients de `@Lazy`

| Avantages | Inconvénients |
|---|---|
| Démarrage plus rapide (beans créés à la demande) | Les erreurs de configuration ne sont détectées qu'au **premier usage** du bean, pas au démarrage |
| Économie de mémoire si certains beans ne sont jamais utilisés | Peut rendre le diagnostic plus difficile en production |

**Mode global** : `spring.main.lazy-initialization=true` dans `application.properties`
rend *tous* les beans lazy.

A utiliser avec parcimonie : le gain de démarrage se fait au prix d'une fiabilité
réduite (pas de fail-fast).

---

## Question 6 — `/check` retourne `true` puis `false`

| Résultat | Scope | Ce qui se passe |
|---|---|---|
| `true` | **singleton** (défaut) | Une seule instance de `CricketCoach` dans le conteneur — les deux références pointent vers le même objet. |
| `false` | **prototype** | Le conteneur crée une **nouvelle instance** à chaque injection — les deux références sont des objets distincts. |

**Changement effectué** : ajout de `@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)` sur `CricketCoach`.

---

## Question 7 — Cycle de vie d'un bean singleton

```
1. Constructeur        -> instanciation par Spring
2. Injection           -> dépendances fournies (@Autowired)
3. @PostConstruct      -> méthode d'initialisation personnalisée
4. ... utilisation ...
5. @PreDestroy         -> méthode de nettoyage (avant destruction du contexte)
```

**Pourquoi `@PreDestroy` n'est-il pas appelé pour un bean prototype ?**
Spring crée les beans prototype à la demande mais **ne les suit plus** ensuite.
Il ne connaît pas leur durée de vie ni leur moment de libération → il ne peut
pas appeler `@PreDestroy`. La gestion du cycle de vie complet est à la charge du code appelant.

---

## Question 8 — Quand préférer `@Bean` dans `@Configuration` à `@Component`

| Cas | Exemple |
|---|---|
| **Classe tierce non modifiable** (bibliothèque externe, pas accès au source) | `SwimCoach` → on ne peut pas ajouter `@Component` |
| **Création complexe** (factory method, paramètres, post-traitement) | `@Bean public DataSource ds() { ... }` |
| **Plusieurs instances** du même type avec des configurations différentes | `@Bean("aquatic")` et `@Bean("fluvial")` retournant deux `SwimCoach` différents |

---

## Tableau des scopes (exercice 5.6)

| Scope | Description |
|---|---|
| `singleton` (défaut) | Une seule instance partagée dans tout le conteneur Spring |
| `prototype` | Nouvelle instance créée à **chaque injection / demande** au conteneur |
| `request` | Une instance par **requête HTTP** (contexte web uniquement) |
| `session` | Une instance par **session HTTP** (contexte web uniquement) |
| `application` | Une instance par **ServletContext** (contexte web uniquement) |
