package com.parcelapp.courier.service

import com.parcelapp.courier.client.AuthClient
import com.parcelapp.courier.entity.Courier
import com.parcelapp.courier.mapper.CourierMapper
import com.parcelapp.courier.model.AddMemberToGroupRequest
import com.parcelapp.courier.model.CreateCourierModel
import com.parcelapp.courier.model.CreateUserModel
import com.parcelapp.courier.model.UserModel
import com.parcelapp.courier.repository.CourierRepository
import com.parcelapp.courier.spec.CourierFilterSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static com.parcelapp.courier.entity.Courier.CourierStatus.AVAILABLE
import static com.parcelapp.courier.entity.Courier.CourierType.CAR_COURIER
import static com.parcelapp.courier.entity.Courier.CourierType.WALKING_COURIER
import static com.parcelapp.courier.model.AddMemberToGroupRequest.UserGroup.COURIER

@SpringBootTest
@ActiveProfiles("test")
class CouriersServiceTest extends Specification {

    private CouriersService couriersService

    private CourierRepository courierRepository
    private AuthClient authClient

    @Autowired
    private CourierMapper mapper

    void setup() {
        courierRepository = Mock(CourierRepository.class)
        authClient = Mock(AuthClient.class)
        couriersService = new CouriersService(courierRepository, authClient, mapper)
    }

    def "get couriers"() {
        given: "courier type"
        def type = CAR_COURIER

        and: "courier status"
        def status = AVAILABLE

        and: "page request"
        def pageable = PageRequest.of(0, 20)

        and: "couriers"
        def courier = new Courier()
        courier.id = 1
        courier.status = status
        courier.type = type
        courier.userId = "1"
        def otherCourier = new Courier()
        otherCourier.id = 2
        otherCourier.status = status
        otherCourier.type = type
        otherCourier.userId = "2"

        when: "getting couriers"
        def couriers = couriersService.getCouriers(type, status, pageable)

        then: "got couriers"
        1 * courierRepository.findAll(new CourierFilterSpecification(type, status), pageable) >> new PageImpl<>(List.of(courier, otherCourier))

        and:
        def content = couriers.content
        content.size() == 2
        content.get(0).id == 1
        content.get(0).status == status
        content.get(0).type == type
        content.get(0).userId == "1"
        content.get(1).id == 2
        content.get(1).status == status
        content.get(1).type == type
        content.get(1).userId == "2"
    }

    def "find courier by id"() {
        given: "courier id"
        def courierId = 1L

        and: "courier"
        def courier = new Courier()
        courier.id = courierId
        courier.status = AVAILABLE
        courier.type = WALKING_COURIER
        courier.userId = "1"

        when: "getting the courier by id"
        def result = couriersService.getCourier(courierId)

        then: "got the courier"
        1 * courierRepository.findById(courierId) >> Optional.of(courier)

        and:
        result.id == courierId
        result.status == AVAILABLE
        result.type == WALKING_COURIER
        result.userId == "1"
    }

    def "create courier"() {
        given: "create user request"
        def createUserModel = new CreateUserModel()
        createUserModel.userName = "courier_user"
        createUserModel.password = "password"
        createUserModel.firstName = "Courier"
        createUserModel.lastName = "User"
        createUserModel.emailAddress = "courier@example.com"

        and: "user"
        def user = new UserModel()
        user.id = "1"
        user.userName = "courier_user"

        and: "courier type"
        def courierType = CAR_COURIER

        and: "courier"
        def courier = new Courier()
        courier.userId = "1"
        courier.type = courierType
        courier.status = AVAILABLE

        and: "create courier request"
        def createCourierModel = new CreateCourierModel()
        createCourierModel.registrationInfo = createUserModel
        createCourierModel.type = courierType

        when: "creating courier"
        def result = couriersService.createCourier(createCourierModel)

        then: "create the courier"
        1 * authClient.createUser(createUserModel) >> user
        1 * authClient.addMemberToGroup(user.id, new AddMemberToGroupRequest(COURIER))
        1 * courierRepository.save(courier) >> courier.withId(1L)

        and:
        result.user.id == "1"
        result.user.userName == "courier_user"
        result.courier.id == 1L
        result.courier.userId == "1"
        result.courier.type == courierType
        result.courier.status == AVAILABLE
    }
}
