package com.parcelapp.order.service

import com.parcelapp.order.entity.DeliveryMovement
import com.parcelapp.order.entity.DeliveryOrder
import com.parcelapp.order.entity.Parcel
import com.parcelapp.order.mapper.DeliveryOrderMapper
import com.parcelapp.order.model.AddressModel
import com.parcelapp.order.model.CourierModel
import com.parcelapp.order.model.CreateDeliveryMovementModel
import com.parcelapp.order.model.CreateDeliveryOrderModel
import com.parcelapp.order.model.CreateParcelModel
import com.parcelapp.order.model.UpdateDeliveryOrderAssigneeModel
import com.parcelapp.order.repository.DeliveryMovementRepository
import com.parcelapp.order.repository.DeliveryOrderRepository
import com.parcelapp.order.repository.ParcelRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.LocalDateTime

import static com.parcelapp.order.entity.DeliveryOrder.DeliveryOrderStatus.CREATED

@SpringBootTest
@ActiveProfiles("test")
class OrdersServiceTests extends Specification {

    private OrdersService ordersService;

    private DeliveryOrderRepository deliveryOrderRepository;
    private ParcelRepository parcelRepository;
    private DeliveryMovementRepository deliveryMovementRepository;
    private CouriersService couriersService;

    @Autowired
    private DeliveryOrderMapper mapper;

    private void setup() {
        deliveryOrderRepository = Mock(DeliveryOrderRepository.class)
        parcelRepository = Mock(ParcelRepository.class)
        deliveryMovementRepository = Mock(DeliveryMovementRepository.class)
        couriersService = Mock(CouriersService.class)

        ordersService = new OrdersService(deliveryOrderRepository, parcelRepository, deliveryMovementRepository,
                couriersService, mapper)
    }

    def "create delivery order"() {
        given: "create order model"
        def addressModel = new AddressModel()
        addressModel.addressLine1 = "Address line 1"
        addressModel.city = "Istanbul"
        addressModel.district = "Maltepe"
        addressModel.countryCode = "TR"
        addressModel.zipCode = "34000"
        addressModel.receiverName = "Name"
        addressModel.receiverSurname = "Surname"
        addressModel.receiverPhone = "+905554443322"

        def parcelModel = new CreateParcelModel()
        parcelModel.type = Parcel.ParcelType.MEDIUM
        parcelModel.weight = 2.500

        def otherParcelModel = new CreateParcelModel()
        otherParcelModel.type = Parcel.ParcelType.LARGE
        otherParcelModel.weight = 5.250

        def createOrderModel = new CreateDeliveryOrderModel()
        createOrderModel.destination = addressModel
        createOrderModel.parcels = List.of(parcelModel, otherParcelModel)

        and: "price"
        def price = OrdersService.calculatePrice(createOrderModel)

        and: "order entity"
        def address = new DeliveryOrder.Address()
        address.addressLine1 = "Address line 1"
        address.city = "Istanbul"
        address.district = "Maltepe"
        address.countryCode = "TR"
        address.zipCode = "34000"
        address.receiverName = "Name"
        address.receiverSurname = "Surname"
        address.receiverPhone = "+905554443322"

        def parcel = new Parcel()
        parcel.type = Parcel.ParcelType.MEDIUM
        parcel.weight = 2.500

        def otherParcel = new Parcel()
        otherParcel.type = Parcel.ParcelType.LARGE
        otherParcel.weight = 5.250

        def order = new DeliveryOrder()
        order.destination = address
        order.parcels = List.of(parcel, otherParcel)
        order.status = CREATED
        order.price = price

        when: "creating order"
        def result = ordersService.createDeliveryOrder(createOrderModel)

        then: "create order and parcels successfully"
        1 * deliveryOrderRepository.save(order) >> order.withId(1L)
        1 * parcelRepository.saveAll(_)

        and:
        def resultAddress = result.destination
        resultAddress.addressLine1 == "Address line 1"
        resultAddress.city == "Istanbul"
        resultAddress.district == "Maltepe"
        resultAddress.countryCode == "TR"
        resultAddress.zipCode == "34000"
        resultAddress.receiverName == "Name"
        resultAddress.receiverSurname == "Surname"
        resultAddress.receiverPhone == "+905554443322"

        result.id == 1
        result.status == CREATED
        result.price == price
    }

    def "get delivery orders"() {
        given: "order"
        def address = new DeliveryOrder.Address()
        address.addressLine1 = "Address line 1"
        address.city = "Istanbul"
        address.district = "Maltepe"
        address.countryCode = "TR"
        address.zipCode = "34000"
        address.receiverName = "Name"
        address.receiverSurname = "Surname"
        address.receiverPhone = "+905554443322"

        def parcel = new Parcel()
        parcel.type = Parcel.ParcelType.MEDIUM
        parcel.weight = 2.500

        def otherParcel = new Parcel()
        otherParcel.type = Parcel.ParcelType.LARGE
        otherParcel.weight = 5.250

        def order = new DeliveryOrder()
        order.destination = address
        order.parcels = List.of(parcel, otherParcel)
        order.status = CREATED
        order.price = BigDecimal.TEN

        and: "page request"
        def pageable = PageRequest.of(0, 10)

        when: "getting orders"
        def orders = ordersService.getDeliveryOrders(pageable)

        then: "got the orders"
        1 * deliveryOrderRepository.findAll(pageable) >> new PageImpl<>(List.of(order))

        and:
        def content = orders.getContent()
        content.size() == 1
        content[0].destination.addressLine1 == "Address line 1"
        content[0].destination.city == "Istanbul"
        content[0].destination.district == "Maltepe"
        content[0].destination.countryCode == "TR"
        content[0].destination.zipCode == "34000"
        content[0].destination.receiverName == "Name"
        content[0].destination.receiverSurname == "Surname"
        content[0].destination.receiverPhone == "+905554443322"
        content[0].status == CREATED
        content[0].price == BigDecimal.TEN
    }

    def "create delivery movement"() {
        given: "order id"
        def orderId = 1L

        and: "order"
        def address = new DeliveryOrder.Address()
        address.addressLine1 = "Address line 1"
        address.city = "Istanbul"
        address.district = "Maltepe"
        address.countryCode = "TR"
        address.zipCode = "34000"
        address.receiverName = "Name"
        address.receiverSurname = "Surname"
        address.receiverPhone = "+905554443322"

        def order = new DeliveryOrder()
        order.id = orderId
        order.destination = address
        order.status = CREATED
        order.price = BigDecimal.TEN

        and: "movement"
        def movementDate = LocalDateTime.now()
        def location = new DeliveryMovement.Location()
        location.latitude = 40.99
        location.longitude = 29.16
        location.countryCode = "TR"
        location.city = "Istanbul"
        location.district = "Kadikoy"
        def movement = new DeliveryMovement()
        movement.deliveryOrder = order
        movement.description = "test"
        movement.movementDate = movementDate
        movement.location = location

        and: "create movement request"
        def locationModel = new CreateDeliveryMovementModel.LocationModel()
        locationModel.latitude = 40.99
        locationModel.longitude = 29.16
        locationModel.countryCode = "TR"
        locationModel.city = "Istanbul"
        locationModel.district = "Kadikoy"
        def movementModel = new CreateDeliveryMovementModel()
        movementModel.description = "test"
        movementModel.movementDate = movementDate
        movementModel.location = locationModel

        and: "repository returning the order"
        deliveryOrderRepository.findById(order.id) >> Optional.of(order)

        when: "create delivery movement"
        def result = ordersService.createDeliveryMovement(orderId, movementModel)

        then: "delivery movement created"
        1 * deliveryMovementRepository.save(movement.withDeliveryOrder(order)) >> movement.withId(1L)

        and:
        result.location.latitude == 40.99
        result.location.longitude == 29.16
        result.location.countryCode == "TR"
        result.location.city == "Istanbul"
        result.location.district == "Kadikoy"
        result.description == "test"
        result.movementDate == movementDate
    }

    def "update assigned courier of delivery order"() {
        given: "order id"
        def orderId = 1L

        and: "order"
        def address = new DeliveryOrder.Address()
        address.addressLine1 = "Address line 1"
        address.city = "Istanbul"
        address.district = "Maltepe"
        address.countryCode = "TR"
        address.zipCode = "34000"
        address.receiverName = "Name"
        address.receiverSurname = "Surname"
        address.receiverPhone = "+905554443322"

        def order = new DeliveryOrder()
        order.id = orderId
        order.destination = address
        order.status = CREATED
        order.price = BigDecimal.TEN

        and: "courier id"
        def courierId = 1L

        and: "order assignee update request"
        def updateDeliveryOrderAssigneeRequest = new UpdateDeliveryOrderAssigneeModel()
        updateDeliveryOrderAssigneeRequest.assignedCourierId = courierId

        and: "courier"
        def courier = new CourierModel()
        courier.id = courierId
        courier.type = CourierModel.CourierType.MOTO_COURIER
        courier.status = CourierModel.CourierStatus.AVAILABLE

        and: "service returning the courier"
        couriersService.getCourier(courierId) >> courier

        and: "repository returning the order"
        deliveryOrderRepository.findById(orderId) >> Optional.of(order)

        when: "assigning order to courier"
        def result = ordersService.updateDeliveryOrderAssignee(orderId, updateDeliveryOrderAssigneeRequest)

        then: "assign delivery order to courier"
        1 * deliveryOrderRepository.save(order.withAssignedCourierId(courierId)) >> order.withAssignedCourierId(courierId)

        and:
        result.assignedCourierId == courierId
    }
}
