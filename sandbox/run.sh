# Criar as docker networks
docker network create adm_videos_services
docker network create elastic

# Criar as pastas com permissões
mkdir -m 777 .docker
mkdir -m 777 .docker/es01
mkdir -m 777 .docker/keycloak

docker compose -f services/docker-compose.yml up -d
docker compose -f elk/docker-compose.yml up -d
#docker compose -f app/docker-compose.yml up -d

echo "Inicializando os containers..."
sleep 20