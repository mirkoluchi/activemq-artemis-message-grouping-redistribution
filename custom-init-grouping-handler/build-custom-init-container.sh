set -e

read -p "Enter container registry URL (default localhost:32000) " REGISTRY
if [ -z "$REGISTRY" ]; then
  REGISTRY="localhost:32000"
fi;

read -p "Enter image name (default siav-artemis-init-grouping-handler) " IMAGE
if [ -z "$IMAGE" ]; then
  IMAGE="siav-artemis-init-grouping-handler"
fi;

read -p "Enter image version (default 1.0): " VERSION
if [ -z "$VERSION" ]; then
  VERSION="1.0"
fi;

IMAGE_TAG="$REGISTRY/$IMAGE:$VERSION"

echo "Building and pushing image $IMAGE_TAG..."
docker build . -t "$IMAGE_TAG"
docker push "$IMAGE_TAG"
echo "Done"
