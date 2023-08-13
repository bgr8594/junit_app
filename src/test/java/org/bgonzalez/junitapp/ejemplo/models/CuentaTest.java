package org.bgonzalez.junitapp.ejemplo.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Benito", new BigDecimal("1000.12345"));
        //cuenta.setPersona("Benito");
        String esperado ="Benito";
        String real =cuenta.getPersona();
        assertEquals(esperado,real);
        assertTrue(real.equals("Benito"));
    }

    @Test
    void testSaldoCuenta(){
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal(8900.9997));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal(8900.9997));
        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);

    }
}