package org.bgonzalez.junitapp.ejemplo.models;

import org.bgonzalez.junitapp.ejemplo.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;
    @BeforeEach
    void initMetodoTest(){
        this.cuenta = new Cuenta("Benito", new BigDecimal("1000.12345"));

        System.out.println("Iniciando el método");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("Finalizando el test");
    }

    @AfterEach
    void tearDown(){
        System.out.println("Finalizando el método");

    }
    @Nested
    @DisplayName("probando atributos de cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("el nombre")
        void testNombreCuenta() {
            //cuenta.setPersona("Benito");
            String esperado = "Benito";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba " + esperado
                    + " sin embargo fue " + real);
            assertTrue(real.equals("Benito"), () -> "Esperado debe ser igual al real");
        }

        @Test
        @DisplayName("el saldo")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probando referencia o instancias que sean iguales, con el método equals")
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal(8900.9997));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal(8900.9997));
            //assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);

        }

        @Nested
        class CuentaOperacionesTest {
            @Test
            void testDebitoCuenta() {
                Cuenta cuenta = new Cuenta("Benito", new BigDecimal("1000.12345"));
                cuenta.debito(new BigDecimal(100));
                assertNotNull(cuenta.getSaldo());
                assertEquals(900, cuenta.getSaldo().intValue());
                assertEquals("900.12345", cuenta.getSaldo().toPlainString());
            }

            @Test
            void testCreditoCuenta() {
                Cuenta cuenta = new Cuenta("Benito", new BigDecimal("1000.12345"));
                cuenta.credito(new BigDecimal(100));
                assertNotNull(cuenta.getSaldo());
                assertEquals(1100, cuenta.getSaldo().intValue());
                assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
            }

            @Test
            void testTransferirDineroCuentas() {
                Cuenta cuenta1 = new Cuenta("Pedro", new BigDecimal("2500"));
                Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));
                Banco banco = new Banco();
                banco.setNombre("Banco del estado");
                banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

                assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
                assertEquals("3000", cuenta1.getSaldo().toPlainString());
            }
        }


        @Test
        void testDineroInsuficienteException() {
            Cuenta cuenta = new Cuenta("Benito", new BigDecimal("1000.12345"));
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal(1500));
            });
            String actual = exception.getMessage();
            String esperado = "Dinero insuficiente";
            assertEquals(esperado, actual);
        }

        @Test
        //@Disabled
        @DisplayName("Probando relaciones entre las cuents y el banco con assertAll")
        void testRelacionBancoCuentas() {
            //fail();
            Cuenta cuenta1 = new Cuenta("Pedro", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));
            Banco banco = new Banco();

            banco.add(cuenta1);
            banco.add(cuenta2);

            banco.setNombre("Banco del estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

            assertAll(
                    () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                            () -> "El valor del saldo de la cuenta2 no es el esperado")
                    ,
                    () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                            () -> "El valor del saldo de la cuenta1 no es el esperado"),
                    () -> assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene las cuentas esperadas"),
                    () -> assertEquals("Banco del estado", cuenta1.getBanco().getNombre(), () -> "El banco del estado no es el esperado"),
                    () -> assertEquals("Pedro", banco.getCuentas().stream().
                            filter(cuenta -> cuenta.getPersona().equals("Pedro"))
                            .findFirst()
                            .get().getPersona(), () -> "La cuenta de Pedro no forma parte del banco " + banco.getNombre())

                    ,
                    () -> {
                        assertTrue(banco.getCuentas().stream().
                                filter(cuenta -> cuenta.getPersona().equals("Pedro"))
                                .findFirst()
                                .isPresent(), () -> "El banco " + banco.getNombre() + " no tiene una cuenta de Pedro");
                    },
                    () -> assertTrue(banco.getCuentas().stream().anyMatch(cuenta -> cuenta.getPersona().equals("Pedro")),
                            () -> "El " + banco.getNombre() + " no tiene una cuenta de Pedro"
                    )
            );

        }

        @Nested
        class SistemaOperativoTest {
            @Test
            @EnabledOnOs(OS.WINDOWS)
            void testSoloWindows() {

            }

            @Test
            @EnabledOnOs({OS.LINUX, OS.MAC})
            void testSoloLinuxMac() {

            }

            @Test
            @DisabledOnOs(OS.WINDOWS)
            void testNoWindows() {

            }
        }

        class JavaVersionTest {
            @Test
            @EnabledOnJre(JRE.JAVA_8)
            void soloJdk8() {

            }

            @Test
            @EnabledOnJre(JRE.JAVA_11)
            void testSoloJdk11() {

            }

            @Test
            @DisabledOnJre(JRE.JAVA_11)
            void testNoJdk11() {

            }

        }

        @Nested
        class SystemPropertiesTest {
            @Test
            void imprimirSystemProperties() {
                Properties properties = System.getProperties();
                properties.forEach((k, v) -> System.out.println(k + " :" + v));
            }

            @Test
            @EnabledIfSystemProperty(named = "java.version", matches = "14.0.12")
            public void testJavaVersion() {

            }

            @Test
            @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
            void testSolo64() {
            }


            @Test
            @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
            void testNoSolo64() {
            }

            @Test
            @EnabledIfSystemProperty(named = "user.name", matches = "venom")
            void testUsername() {

            }

            @Test
            @EnabledIfSystemProperty(named = "ENV", matches = "dev")
            void testDev() {
            }
        }

        @Nested
        class VariablesAmbienteTest {
            @Test
            void imprimirVariablesAmbiente() {
                System.out.println("imprimirVariablesAmbiente");
                System.getenv().forEach((k, v) -> {
                    System.out.println(k + "=" + v);
                });
            }

            @Test
            @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "*")
            void testJavaHome() {

            }

            @Test
            @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "1")
            void procesadores() {

            }

            @Test
            @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
            void testEnv() {

            }

            @Test
            @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
            void testEnvProdDisabled() {
                System.out.println("testEnvProdDisabled");
            }
        }

        @Nested
        class AssumeTest {
            @Test
            @DisplayName("testSaldoCuentaDev")
            void testSaldoCuentaDev() {
                boolean dev = "dev".equals(System.getProperty("ENV"));
                assumeTrue(dev);
                assertNotNull(cuenta.getSaldo());
                assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
                assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            }

            @Test
            @DisplayName("testSaldoCuentaDev2")
            void testSaldoCuentaDev2() {
                boolean esDev = "dev".equals(System.getProperty("ENV"));
                assumingThat(esDev, () -> {
                    assertNotNull(cuenta.getSaldo());
                    assertEquals(1000.123456, cuenta.getSaldo().doubleValue());
                    assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
                    assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
                });
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
                assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            }
        }
    }

    @DisplayName("Probando debito cuenta repetir!")
    @RepeatedTest(value=5, name="{displayName}-Repetición numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repeticion ="+info.getCurrentRepetition());
        }
        Cuenta cuenta = new Cuenta("Benito", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }
}