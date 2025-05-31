package airline.util;

import airline.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PlaneFactory} class.
 * Tests creation of all supported plane types, the available types list,
 * and behavior when an unknown type is requested.
 */
class PlaneFactoryTest {

    /**
     * Tests creation of a PassengerPlane using the factory.
     */
    @Test
    void testCreatePassengerPlane() {
        Plane plane = PlaneFactory.createPlane(
                "Passenger", "Boeing 737", 180, 20, 3500, 2.7, 800, 900, 12000
        );
        assertTrue(plane instanceof PassengerPlane);
        assertEquals("Boeing 737", plane.getModel());
        assertEquals(180, plane.getCapacity());
        assertEquals(20, plane.getCargoCapacity());
        assertEquals(3500, plane.getRange());
        assertEquals(2.7, plane.getFuelConsumption());
        assertEquals(800, plane.getCruisingSpeed());
        assertEquals(900, plane.getMaxSpeed());
        assertEquals(12000, plane.getServiceCeiling());
        assertEquals("Passenger", plane.getType());
    }

    /**
     * Tests creation of a CargoPlane using the factory.
     */
    @Test
    void testCreateCargoPlane() {
        Plane plane = PlaneFactory.createPlane(
                "Cargo", "Antonov AN-124", 0, 120, 4800, 9.8, 750, 850, 12000
        );
        assertTrue(plane instanceof CargoPlane);
        assertEquals("Antonov AN-124", plane.getModel());
        assertEquals(120, plane.getCargoCapacity());
        assertEquals(4800, plane.getRange());
        assertEquals(9.8, plane.getFuelConsumption());
        assertEquals(750, plane.getCruisingSpeed());
        assertEquals(850, plane.getMaxSpeed());
        assertEquals(12000, plane.getServiceCeiling());
        assertEquals("Cargo", plane.getType());
    }

    /**
     * Tests creation of a BusinessJet using the factory.
     */
    @Test
    void testCreateBusinessJet() {
        Plane plane = PlaneFactory.createPlane(
                "Business Jet", "Gulfstream G650", 18, 5, 12900, 2.5, 900, 980, 15545
        );
        assertTrue(plane instanceof BusinessJet);
        assertEquals("Gulfstream G650", plane.getModel());
        assertEquals(18, plane.getCapacity());
        assertEquals(5, plane.getCargoCapacity());
        assertEquals("Business Jet", plane.getType());
    }

    /**
     * Tests creation of a LightPlane using the factory.
     */
    @Test
    void testCreateLightPlane() {
        Plane plane = PlaneFactory.createPlane(
                "Light Plane", "Cessna 172", 4, 0.5, 1280, 0.25, 226, 302, 4100
        );
        assertTrue(plane instanceof LightPlane);
        assertEquals("Cessna 172", plane.getModel());
        assertEquals(4, plane.getCapacity());
        assertEquals(0.5, plane.getCargoCapacity());
        assertEquals("Light Plane", plane.getType());
    }

    /**
     * Tests creation of a Fighter using the factory.
     */
    @Test
    void testCreateFighter() {
        Plane plane = PlaneFactory.createPlane(
                "Fighter", "F-16", 0, 8.7, 4200, 3.9, 1500, 2000, 15240
        );
        assertTrue(plane instanceof Fighter);
        assertEquals("F-16", plane.getModel());
        assertEquals(8.7, plane.getCargoCapacity());
        assertEquals("Fighter", plane.getType());
    }

    /**
     * Tests creation of a Bomber using the factory.
     */
    @Test
    void testCreateBomber() {
        Plane plane = PlaneFactory.createPlane(
                "Bomber", "B-52", 0, 32, 14000, 12, 845, 1000, 16700
        );
        assertTrue(plane instanceof Bomber);
        assertEquals("B-52", plane.getModel());
        assertEquals(32, plane.getCargoCapacity());
        assertEquals("Bomber", plane.getType());
    }

    /**
     * Tests creation of an AttackAircraft using the factory.
     */
    @Test
    void testCreateAttackAircraft() {
        Plane plane = PlaneFactory.createPlane(
                "Attack Aircraft", "A-10", 0, 7.3, 1240, 2.5, 676, 706, 13700
        );
        assertTrue(plane instanceof AttackAircraft);
        assertEquals("A-10", plane.getModel());
        assertEquals(7.3, plane.getCargoCapacity());
        assertEquals("Attack Aircraft", plane.getType());
    }

    /**
     * Tests creation of an Interceptor using the factory.
     */
    @Test
    void testCreateInterceptor() {
        Plane plane = PlaneFactory.createPlane(
                "Interceptor", "MiG-31", 0, 5, 1250, 3.1, 1500, 3000, 20600
        );
        assertTrue(plane instanceof Interceptor);
        assertEquals("MiG-31", plane.getModel());
        assertEquals(5, plane.getCargoCapacity());
        assertEquals("Interceptor", plane.getType());
    }

    /**
     * Tests that creating a plane with an unknown type throws IllegalArgumentException.
     */
    @Test
    void testCreatePlaneWithUnknownType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                PlaneFactory.createPlane(
                        "UFO", "AlienCraft", 0, 0, 0, 0, 0, 0, 0
                )
        );
        assertTrue(exception.getMessage().contains("Невідомий тип літака"));
    }

    /**
     * Tests the getAvailableTypes method returns all supported types.
     */
    @Test
    void testGetAvailableTypes() {
        List<String> types = PlaneFactory.getAvailableTypes();
        assertEquals(8, types.size());
        assertTrue(types.contains("Passenger"));
        assertTrue(types.contains("Cargo"));
        assertTrue(types.contains("Business Jet"));
        assertTrue(types.contains("Light Plane"));
        assertTrue(types.contains("Fighter"));
        assertTrue(types.contains("Bomber"));
        assertTrue(types.contains("Attack Aircraft"));
        assertTrue(types.contains("Interceptor"));

        // List is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> types.add("Test"));
    }
}
