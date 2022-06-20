call .\gradlew.bat bootJar -x test -p .\gateway
call .\gradlew.bat bootJar -x test -p .\auth-service
call .\gradlew.bat bootJar -x test -p .\orders-service
call .\gradlew.bat bootJar -x test -p .\couriers-service

docker build --tag parcel-delivery-app/uaa:1.0.0 .\uaa
docker build --tag parcel-delivery-app/gateway:1.0.0 .\gateway
docker build --tag parcel-delivery-app/auth-service:1.0.0 .\auth-service
docker build --tag parcel-delivery-app/orders-service:1.0.0 .\orders-service
docker build --tag parcel-delivery-app/couriers-service:1.0.0 .\couriers-service
