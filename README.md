# TP2_JEE — Questions de compréhension

---

## Question 1 — Différence entre IoC et DI

**IoC (Inversion of Control)** est un principe de conception : au lieu que l'objet crée lui-même ses dépendances avec `new`, c'est un **conteneur externe** (le Spring IoC Container / `ApplicationContext`) qui se charge de les créer et de les fournir.

**DI (Dependency Injection)** est le **mécanisme concret** par lequel Spring réalise cet IoC : il injecte automatiquement les beans nécessaires dans les autres beans, via le constructeur, un setter ou un champ.

**En quoi le conteneur « inverse-t-il le contrôle » ?**

Sans Spring (approche classique) :
```java
// C'est le développeur qui décide quelle implémentation utiliser
Coach myCoach = new CricketCoach();
```
Le contrôle est entre les mains de la classe appelante : elle choisit, crée et gère l'objet.

Avec Spring (IoC) :
```java
// Spring choisit, crée et injecte le bon bean automatiquement
public DemoController(Coach theCoach) {
    this.myCoach = theCoach;
}
```
Le contrôle est **inversé** : ce n'est plus `DemoController` qui décide de l'implémentation, c'est le conteneur Spring qui la choisit, la crée et la fournit. Le développeur ne fait que déclarer ses besoins (« j'ai besoin d'un `Coach` »), et Spring s'occupe du reste.

---

## Question 2 — Pourquoi l'injection par constructeur est recommandée

L'injection par constructeur est préférée à l'injection par champ (`@Autowired` sur un attribut) pour au moins **quatre raisons** :

| Argument | Explication |
|---|---|
| **Immuabilité** | Les dépendances injectées par constructeur peuvent être déclarées `final`. Une fois l'objet construit, elles ne changent plus jamais. Avec l'injection par champ, on ne peut pas utiliser `final`. |
| **Testabilité** | Dans un test unitaire, on peut instancier la classe directement avec un mock passé en paramètre du constructeur, **sans avoir besoin du contexte Spring**. Avec l'injection par champ, il faut obligatoirement un framework de réflexion ou le conteneur Spring pour injecter les dépendances. |
| **Objet toujours valide** | Le constructeur garantit que toutes les dépendances obligatoires sont fournies dès la création. Il est impossible d'avoir un bean dans un état « partiellement initialisé » (certains champs à `null`). |
| **Détection précoce des erreurs** | Spring détecte les dépendances manquantes ou circulaires dès le démarrage de l'application, pas à l'exécution. |

---

## Question 3 — `NoUniqueBeanDefinitionException`

### Situation précise
On obtient cette exception quand **plusieurs beans du même type** existent dans le conteneur et que Spring ne sait pas lequel injecter. Par exemple, si on a quatre classes (`CricketCoach`, `BaseballCoach`, `TennisCoach`, `TrackCoach`) toutes annotées `@Component` et implémentant `Coach`, et qu'on demande l'injection d'un `Coach` sans préciser lequel :

```
NoUniqueBeanDefinitionException: No qualifying bean of type '...Coach'
available: expected single matching bean but found 4
```

### Deux façons de résoudre

| Solution | Mécanisme |
|---|---|
| **`@Qualifier("cricketCoach")`** | On désigne **explicitement** le bean voulu par son id, sur le paramètre du constructeur. |
| **`@Primary`** | On marque **un seul** bean comme candidat par défaut. Spring le choisit automatiquement quand aucun `@Qualifier` n'est précisé. |

### Priorité
Si les deux sont présentes en même temps, **`@Qualifier` gagne sur `@Primary`**. Exemple : si `TrackCoach` est `@Primary` mais que le constructeur a `@Qualifier("cricketCoach")`, c'est `CricketCoach` qui sera injecté.

---

## Question 4 — Id par défaut d'un bean et comment le forcer

### Calcul par défaut
Spring prend le **nom simple de la classe** et met la **première lettre en minuscule** :

| Classe | Id par défaut |
|---|---|
| `CricketCoach` | `cricketCoach` |
| `BaseballCoach` | `baseballCoach` |
| `TrackCoach` | `trackCoach` |
| `TennisCoach` | `tennisCoach` |

Pour une méthode `@Bean`, l'id par défaut est le **nom de la méthode** (ex : `swimCoach()`  → id `swimCoach`).

### Forcer manuellement
```java
@Component("monCoach")          // force l'id à "monCoach"
public class CricketCoach { }

@Bean("aquatic")                // force l'id à "aquatic"
public Coach swimCoach() { }
```

---

## Question 5 — Avantages et inconvénients de `@Lazy`

### Avantages
- **Démarrage plus rapide** : les beans ne sont créés qu'au premier usage, pas tous au lancement de l'application.
- **Économie de mémoire** : si un bean n'est jamais utilisé durant l'exécution, il n'est jamais instancié.

### Inconvénients
- **Erreurs détectées tardivement** : les problèmes de configuration (dépendance manquante, erreur de câblage) ne sont découverts qu'au **premier appel** du bean, pas au démarrage. En production, cela peut provoquer des crashs inattendus sur des chemins de code rarement empruntés.
- **Faux sentiment de sécurité** : l'application démarre sans erreur même si la configuration est cassée.

### Pourquoi « à utiliser avec parcimonie » ?
Le gain en temps de démarrage se fait au prix de la **fiabilité** : on perd le comportement « fail-fast » de Spring qui, par défaut (eager), vérifie tout au démarrage. On réserve `@Lazy` aux cas où le bean est coûteux à créer et rarement utilisé.

**Mode global** : `spring.main.lazy-initialization=true` dans `application.properties` rend tous les beans lazy. À éviter en production.

---

## Question 6 — `/check` renvoie `true` puis `false`

### Ce qui a été changé
On a ajouté `@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)` sur la classe `CricketCoach`.

### Ce que cela prouve

| Résultat de `/check` | Scope | Explication |
|---|---|---|
| `true` | **singleton** (défaut) | Le conteneur ne crée qu'**une seule instance** de `CricketCoach`. Les deux variables `checkCoach1` et `checkCoach2` pointent vers le **même objet en mémoire** → la comparaison `==` retourne `true`. |
| `false` | **prototype** | Le conteneur crée une **nouvelle instance** à chaque injection. `checkCoach1` et `checkCoach2` sont deux objets **distincts** en mémoire → la comparaison `==` retourne `false`. |

Cela prouve que la **portée (scope)** du bean détermine si Spring partage une instance unique ou en crée une nouvelle à chaque demande.

---

## Question 7 — Cycle de vie d'un bean singleton

### Ordre exact des étapes

```
1. Instanciation      → le constructeur est appelé (new CricketCoach())
2. Injection          → Spring injecte les dépendances (@Autowired)
3. @PostConstruct     → méthode d'initialisation personnalisée (doMyStartupStuff())
4. … utilisation …    → le bean est prêt et utilisé par l'application
5. @PreDestroy        → méthode de nettoyage avant destruction (doMyCleanupStuff())
```

Observable dans la console :
```
>> Constructeur : CricketCoach       ← étape 1
In doMyStartupStuff()                 ← étape 3
... (l'app tourne) ...                ← étape 4
In doMyCleanupStuff()                 ← étape 5 (au Ctrl+C)
```

### Pourquoi `@PreDestroy` n'est pas appelé pour un bean prototype ?
Spring crée les beans **prototype** à la demande mais **ne les suit plus après la création**. Il ne conserve aucune référence vers eux et ne sait pas quand ils ne sont plus utilisés. Puisque Spring ne gère pas leur destruction, il ne peut pas appeler `@PreDestroy`. C'est au **code appelant** de gérer le nettoyage des ressources si nécessaire.

---

## Question 8 — Trois cas où `@Bean` est préféré à `@Component`

| Cas | Explication | Exemple |
|---|---|---|
| **1. Classe tierce non modifiable** | On ne peut pas ajouter `@Component` sur une classe d'une bibliothèque externe dont on n'a pas le code source. | `SwimCoach` simulant une classe externe : on l'enregistre via `@Bean("aquatic")` dans `SportConfig`. |
| **2. Création complexe (factory)** | Le bean nécessite une logique de construction élaborée : paramètres calculés, conditions, post-traitement. | `@Bean public DataSource dataSource() { ... config complexe ... }` |
| **3. Plusieurs instances du même type** | On veut enregistrer plusieurs beans du même type avec des configurations différentes. Avec `@Component`, on ne peut avoir qu'un bean par classe. | `@Bean("aquatic") public Coach swim1() { ... }` et `@Bean("fluvial") public Coach swim2() { ... }` — deux `SwimCoach` configurés différemment. |

---

## Tableau des scopes

| Scope | Description |
|---|---|
| **singleton** (défaut) | Une seule instance partagée dans tout le conteneur Spring. Toutes les injections reçoivent le même objet. |
| **prototype** | Nouvelle instance créée à **chaque injection / demande** au conteneur. Spring ne gère pas la destruction. |
| **request** | Une instance par **requête HTTP** (contexte web uniquement). Détruite à la fin de la requête. |
| **session** | Une instance par **session HTTP** (contexte web uniquement). Détruite à l'expiration de la session. |
| **application** | Une instance par **ServletContext** (contexte web uniquement). Partagée entre toutes les sessions et requêtes. |
