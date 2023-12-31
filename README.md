# Rinha de Backend (sem Cache - somente acesso ao postgres)

Desafio original:
https://github.com/zanfranceschi/rinha-de-backend-2023-q3?s=08



## ✔️ Required
* Maven: 3.8.4
* Java version: 17
* Docker version: 20.10.17
* Docker-compose version: v2.2.2

# Sobre:
Projeto backend feito em Quarkus 3.3.1 seguindo as regras:
https://github.com/zanfranceschi/rinha-de-backend-2023-q3/blob/main/INSTRUCOES.md

## Atenção!!!
Antes do build criar arquivo .env com as variáveis: 
* DATABASE_USERNAME= 
* DATABASE_PASSWORD= 
* DATABASE_URL= 
* SERVER_PORT=


## 💻 Getting started

```bash
# Build 
$ mvn clean package

# Local execution
$ mvn quarkus:dev -Ddebug=false
```


## Getting started Docker
```bash

# Started and attaches to containers for a service
$ docker-compose up --build
```


## Getting started Docker (Native Image)
```bash

# Started and attaches to containers for a service
$ docker-compose -f docker-compose-native.yml --env-file ./.env up
```






## Ajustes realizados:

* Ajuste no Script DDL com obrigatoriedade somente no campo apelido
* Ajuste no cache local para 100k (Total de registros do teste de stress)
* Busca de dados no cache, caso contrário, faz busca no bando de dados
* Aumento de sessões do Postgres para 210 e cada POD com 100 sessões simultâneas



## Resultado obtidos

* Docker stats:

![image](https://github.com/zsantana/rinha-backend-by-bruno-borges/assets/17239827/b494d062-c8ad-4299-93cf-c264e68910ee)


* Resultado e performance:

![image](https://github.com/zsantana/rinha-backend-by-bruno-borges/assets/17239827/3675a4b7-6f06-4b55-b09d-64074562aa99)



* Requisições por segundos:
![image](https://github.com/zsantana/rinha-backend-by-bruno-borges/assets/17239827/c4339b73-778c-4cfb-9031-6a49d8b97e15)

