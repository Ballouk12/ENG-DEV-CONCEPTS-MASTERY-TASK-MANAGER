## Description

Ce projet est un labo pratique construit pour consolider des concepts Spring Boot 
utilisés en environnement de production, à travers une petite API Task Manager 
volontairement simple (User → Tasks)

Plutôt que de me concentrer sur les fonctionnalités métier, l'objectif était de 
comprendre et d'implémenter moi-même :

- **Spring Profiles** — séparer la configuration dev / staging / prod (notamment 
  `ddl-auto: update` en dev vs `validate` en staging/prod), sans jamais committer 
  de secret en dur.
- **Flyway** — gérer l'évolution du schéma de base de données par des migrations 
  versionnées (V1, V2...) plutôt que de laisser Hibernate le faire à la volée.
- **@Transactional** — distinguer les transactions en lecture seule des écritures, 
  et gérer l'optimistic locking (@Version) pour détecter les modifications 
  concurrentes sur une même ressource.
- **Cache Redis** — mettre en cache les lectures (@Cacheable) et invalider 
  correctement le cache lors des écritures (@CacheEvict), avec un TTL défini.
- **Spring Security** — protéger des endpoints différemment selon le rôle 
  (USER vs ADMIN), en base pour une future intégration Keycloak/JWT.
