# 🧭 Step-by-step microservices refactor plan
## 🟢 Step 1 — Define service boundaries (design only)

Decide what becomes a service.

From your app:

Person Service
Project Service
Skill Service
Auth Service (your security module)
(Optional) API Gateway

👉 Output of this step: a clear list of services + responsibilities

## 🟡 Step 2 — Split code into separate Spring Boot applications

You create multiple projects (or modules):

Example structure:

cv-microservices/
  person-service/
  project-service/
  skill-service/
  auth-service/
  api-gateway/

👉 Each is now its own Spring Boot app

## 🟡 Step 3 — Move code into correct services

You literally “cut and paste” by domain:

Person service gets:
PersonController
PersonService + impl
PersonRepository
PersonEntity
Person DTOs
Person mappers
Person exceptions

Same for Project and Skill.

👉 Goal: each service owns its full vertical slice

## 🟡 Step 4 — Fix databases (VERY important)

Each service must decide:

own database OR shared database (for learning)

You will define:

schema strategy
JPA usage
table ownership

👉 This step is where architecture starts to matter

## 🟡 Step 5 — Remove cross-entity dependencies

Right now you have:

Person ↔ Project
Person ↔ Skill
Join tables

You must replace this with:

IDs only (recommended for learning)
or service calls

👉 Example:
Person stores:

projectIds
skillIds
## 🟡 Step 6 — Add inter-service communication

Choose ONE approach:

Spring HTTP Interface (@HttpExchange) ✅ recommended
OR OpenFeign
OR RestClient manually

👉 Services start calling each other via HTTP

## 🟡 Step 7 — Add API Gateway

Create gateway service:

routes all external requests
hides internal services
handles JWT validation (optional central place)
## 🟡 Step 8 — Authentication strategy

Decide:

JWT validation in each service (recommended)
or gateway-only validation (simpler but weaker)

👉 Also define how token is forwarded between services

## 🟡 Step 9 — Configuration cleanup

You will separate:

application.yml per service
ports (8081, 8082, 8083…)
service URLs or discovery config
## 🟡 Step 10 — (Optional) Service discovery

Add if needed:

Eureka Server
or static URLs (fine for learning)
## 🟡 Step 11 — Testing flow end-to-end

Verify:

login works
gateway routes requests
services talk to each other
DB operations still work
## 🟡 Step 12 — Polish & documentation

Add:

architecture diagram
explanation of design choices
tradeoffs