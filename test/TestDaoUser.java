package test;
    import controller.DaoUser;
import junit.framework.TestCase;

public class TestDaoUser extends TestCase {
    private DaoUser user;

    public void scenario(){
        user = new DaoUser();
    }
    //****************************testsaveNewUser********************

    public void testSaveNewUser(){ //caso exitoso
        scenario();
        assertEquals("Operacion exitosa",(user.saveNewUser(168,"pipe","malo","451",(short)0,(short)0,true,35)));
    }

    public void testSaveNewUserCreado(){ //usuario existente
        scenario();
        assertEquals("El usuario ya se encuentra creado",(user.saveNewUser(162,"pipe","malo","436",(short)0,(short)0,true,35)));
    }

    //****************************editUser********************
    public void testEditUser(){ //editar exitoso
        scenario();
        assertEquals("Usuario editado con exito",(user.editUser(162,"pipe","malo","435",(short)0,(short)0,true,false)));
    }

    //documento existente toca hacerlo manual

    //****************************loadUser********************
    public void testLoadUser(){ //carga correcta
        scenario();
        assertTrue(169==user.loadUser("436","Cédula de ciudadanía").getId());
    }

    //****************************loginUser********************
    public void testLoginUser(){ //login correcto
        scenario();
        assertEquals(3 , user.loginUser("436", "436"));
    }

    public void testLoginUserIncorrecto(){ //password incorrecto
        scenario();
        assertFalse(3 == user.loginUser("436", "432346"));
    }

    public void testCheckPassword(){ //check correcto
        scenario();
        assertEquals(true , user.checkPassword(169,"436"));
    }





}