package connection;

//import at.favre.lib.crypto.bcrypt.BCrypt;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import view.Login;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class DbManager {
    private DBconnect dBconnect;
    private Connection connection;

    public DbManager(String user, String password, String dataBaseName, String host) {
        dBconnect = new DBconnect(user, password, dataBaseName, host);
        connection = null;
    }

    //**************************** METODOS DEL CLIENTE ********************
    public String saveNewClient(Client client, int currentLoginUser) {
        String saveQuery, selectQuery, logQuery;
        int numRows, id = -1;
        saveQuery = "INSERT INTO public.client(client_name, client_last_name, client_document_number, client_email, client_address, client_type, client_document_type)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";


        selectQuery = "SELECT client_id FROM public.client WHERE client_document_number = ?  AND client_document_type = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, client.getName());
            statement.setString(2, client.getLastName());
            statement.setString(3, client.getDocumentId());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getDirection());
            statement.setShort(6, client.getType());
            statement.setShort(7, client.getDocumentType());
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            statement = connection.prepareStatement(selectQuery);
            statement.setString(1, client.getDocumentId());
            statement.setShort(2, client.getDocumentType());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt(1);
            System.out.println(id);
            logQuery = "INSERT INTO public.client_date(client_id, user_id, join_date) VALUES(?, ?, current_timestamp(0))";
            statement = connection.prepareStatement(logQuery);
            statement.setInt(1, id);
            statement.setInt(2, currentLoginUser);
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            return "Cliente creado con exito";

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Este cliente ya se encuentra registrado";
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Ocurrio un error interno del sistema";
        }
    }

    public String editClient(Client client) {
        int numRows;
        String sql_update = "UPDATE public.client " +
                "SET client_name = ?, client_last_name = ?, client_document_number = ?, client_email = ?, client_address = ?, client_type = ?, client_document_type = ?" +
                " WHERE client_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql_update);
            statement.setString(1, client.getName());
            statement.setString(2, client.getLastName());
            statement.setString(3, client.getDocumentId());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getDirection());
            statement.setShort(6, client.getType());
            statement.setShort(7, client.getDocumentType());
            statement.setInt(8, client.getId());
            numRows = statement.executeUpdate();
            System.out.println("up " + numRows);
            return "Cliente editado exito";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Error al momento de editar el cliente";
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Ocurrio un error interno del sistema";
        }
    }

    public Client loadClient(String documentNumber, short clientDocumentType) {
        System.out.println(documentNumber + " " + clientDocumentType);
        String sql_select = "SELECT client_id, client_name, client_last_name," +
                " client_email, client_address, client_type " +
                "FROM public.client WHERE client_document_number = ? AND client_document_type = ?";
        Client client = new Client();
        try {
            System.out.println("consultando en la base de datos");
            PreparedStatement statement = connection.prepareStatement(sql_select);
            statement.setString(1, documentNumber);
            statement.setShort(2, clientDocumentType);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("All good");
            resultSet.next();
            System.out.println("All good 1");
            client.setId(resultSet.getInt(1));
            client.setName(resultSet.getString(2));
            client.setLastName(resultSet.getString(3));
            client.setDocumentType(clientDocumentType);
            client.setDocumentId(documentNumber);
            client.setEmail(resultSet.getString(4));
            client.setDirection(resultSet.getString(5));
            client.setType(resultSet.getShort(6));
            return client;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println("Ocurrio un error interno del sistema");
        }

        return new Client();
    }

    //**************************** METODOS DEL USUARIO ********************
    public int loginUser(String documento_id_usuario, short documentType, String password) {
        String sql_select = "SELECT user_password, user_type, user_state, up_to_date_password, user_id" +
                " FROM public.user WHERE user_document_number = ? AND user_document_type = ?";

        try {
            System.out.println("consultando en la base de datos");
            PreparedStatement statement = connection.prepareStatement(sql_select);
            statement.setString(1, documento_id_usuario);
            statement.setShort(2, documentType);
            ResultSet resultSet = statement.executeQuery();
            String hashedPasswordFromBD;
            short user_type;
            if (!resultSet.next()) {
                System.out.println("No se pudo encontrar el usuario");
                return -1;
            }

            hashedPasswordFromBD = resultSet.getString(1);
            user_type = resultSet.getShort(2);
            final BCrypt.Result resultCompare = BCrypt.verifyer().verify(password.toCharArray(), hashedPasswordFromBD);
            if (!resultCompare.verified) {
                System.out.println("Contraseña invalida");
                return -2;
            }

            if (!resultSet.getBoolean(3)) {
                System.out.println("Cuenta desactivada");
                return -3;
            }

            Login.currentLoggedUser = resultSet.getInt(5);
            if (!resultSet.getBoolean(4))
                return 3;

            return user_type;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR Fatal en la base de datos");
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        return -4;
    }

    public String saveNewUser(User user, int currentLoginUser) {
        int numRows, id;
        String saveQuery, selectQuery, logQuery;
        final String hashWillBeStored = BCrypt.withDefaults().hashToString(12, user.getDocumentIdNumber().toCharArray());
        saveQuery = "INSERT INTO public.user(user_name, user_last_name, user_document_number, user_type, user_state, user_password, user_document_type)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?)";

        selectQuery = "SELECT user_id FROM public.\"user\" WHERE user_document_number = ? AND user_document_type = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, user.getName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getDocumentIdNumber());
            statement.setShort(4, user.getType());
            statement.setBoolean(5, user.getState());
            statement.setString(6, hashWillBeStored);
            statement.setShort(7, user.getDocumentType());
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            statement = connection.prepareStatement(selectQuery);
            statement.setString(1, user.getDocumentIdNumber());
            statement.setShort(2, user.getDocumentType());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt(1);
            logQuery = "INSERT INTO public.user_creation_date" +
                    " VALUES (?, ?, current_timestamp(0))";
            statement = connection.prepareStatement(logQuery);
            statement.setInt(1, id);
            statement.setInt(2, currentLoginUser);
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            return "Operacion exitosa";

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "El usuario ya se encuentra creado";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Ocurrio un error interno del sistema";
        }
    }

    public String editUser(User user) {
        int numRows;
        if (!user.isPasswordReset()) {
            final String hashWillBeStored = BCrypt.withDefaults().hashToString(12, user.getDocumentIdNumber().toCharArray());
            String sql_update = "UPDATE public.\"user\"" +
                    " SET user_password = ? WHERE user_id = ?";
            try {
                PreparedStatement statement = connection.prepareStatement(sql_update);
                statement.setString(1, hashWillBeStored);
                statement.setInt(2, user.getId());
                System.out.println("user id: " + user.getId());
                numRows = statement.executeUpdate();
                System.out.println("up " + numRows);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return "Error al editar el usuario";
            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                return "Ocurrio un error interno del sistema";
            }
        }

        String sql_update = "UPDATE public.\"user\"" +
                " SET user_name = ?, user_last_name = ?, user_document_number = ?, user_type = ?, user_document_type = ?, user_state = ?" +
                ", up_to_date_password = ? WHERE user_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql_update);
            statement.setString(1, user.getName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getDocumentIdNumber());
            statement.setShort(4, user.getType());
            statement.setShort(5, user.getDocumentType());
            statement.setBoolean(6, user.getState());
            statement.setBoolean(7, user.isPasswordReset());
            statement.setInt(8, user.getId());
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            return "Usuario editado con exito";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Error al editar el usuario";
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Ocurrio un error interno del sistema";
        }
    }

    public User loadUser(String documentNumber, short userDocumentType) {

        String sql_select = "SELECT user_id, user_name, user_last_name, user_document_number, user_document_type," +
                " user_type, user_state, up_to_date_password " +
                "FROM public.user WHERE user_document_number = ? AND user_document_type = ?";
        try {
            System.out.println("Consultando en la base de datos");
            PreparedStatement statement = connection.prepareStatement(sql_select);
            statement.setString(1, documentNumber);
            statement.setShort(2, userDocumentType);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new User(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getShort(5),
                    resultSet.getShort(6),
                    resultSet.getBoolean(7),
                    resultSet.getBoolean(8)
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
        return new User();
    }

    public boolean checkPassword(int userId, String password) {
        String sql_select = "SELECT user_password" +
                " FROM public.user WHERE user_id = ?";

        try {
            System.out.println("Consultando en la base de datos");
            PreparedStatement statement = connection.prepareStatement(sql_select);
            statement.setInt(1, userId);
            ResultSet table = statement.executeQuery();
            String hashedPasswordFromBD;
            if (!table.next()) {
                System.out.println("No se pudo encontrar el usuario");
                return false;
            }
            hashedPasswordFromBD = table.getString(1);
            final BCrypt.Result resultCompare = BCrypt.verifyer().verify(password.toCharArray(), hashedPasswordFromBD);
            if (!resultCompare.verified) {
                System.out.println("CONTRASEÑA INVALIDA");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public short changePassword(int userId, String password) {
        final String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        String sql_update =
                "UPDATE public.user " +
                        "SET user_password = ?, up_to_date_password = true " +
                        "WHERE user_id = ?";

        String sql_select =
                "select user_type from public.user where user_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql_update);
            statement.setString(1, encryptedPassword);
            statement.setInt(2, userId);
            statement.executeUpdate();
            statement = connection.prepareStatement(sql_select);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getShort(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public String saveNewPlan(Plan plan) {

        int numRows;
        String saveQuery = "INSERT INTO public.plan(plan_name, plan_cost, plan_minutes, plan_data_cap, plan_text_message) " +
                "VALUES(?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, plan.getPlanName());
            statement.setDouble(2, plan.getPlanCost());
            statement.setInt(3, plan.getPlanMinutes());
            statement.setInt(4, plan.getPlanData());
            statement.setInt(5, plan.getPlanTextMsn());
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            return "Plan registrado con exito";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Error al crear plan";
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Ocurrio un error interno del sistema";
        }

    }

    public String saveNewVoiceMins(Voice voice) {
        int numRows;
        String saveQuery = "INSERT INTO public.voice(voice_name, voice_minutes) " +
                "VALUES(?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, voice.getVoiceName());
            statement.setInt(2, voice.getVoiceMinutes());
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            return "Operación realizada con exito";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Error al intentar crear los minutos";
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Ocurrio un error interno del sistema";
        }
    }

    public String saveApp(App app) {
        int numRows;
        String saveQuery = "INSERT INTO public.apps(app_name, app_mb_cap) " +
                "VALUES(?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, app.getAppName());
            statement.setInt(2, app.getAppMb());
            numRows = statement.executeUpdate();
            System.out.println(numRows);
            return "Operación realizada con exito";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Error al intentar crear los megas";
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Ocurrio un error interno del sistema";
        }
    }

    public ObservableList<PlanTable> listExtras() {
        String sql_select1 = "SELECT voice_name, voice_minutes" +
                " FROM public.voice";
        String sql_select2 = "SELECT app_name, app_mb_cap " +
                " FROM public.apps";

        ObservableList<PlanTable> result = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet table = statement.executeQuery(sql_select1);
            while (table.next()) {
                result.add(new PlanTable(table.getString(1), table.getInt(2), false, 0));
            }
            table = statement.executeQuery(sql_select2);
            while (table.next()) {
                result.add(new PlanTable(table.getString(1), table.getInt(2), false, 1));
            }
            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return result;
    }


    //**************************** METODOS DEL BANCO ********************
    public String saveBank(String bankName, String accountNumber, String bankNIT) {
        int numRows;
        String sql = "INSERT INTO public.bank(bank_name, account_number, state, bank_nit) VALUES(?,?,true,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, bankName);
            preparedStatement.setString(2, accountNumber);
            preparedStatement.setString(3, bankNIT);
            numRows = preparedStatement.executeUpdate();
            if (numRows > 0) {
                return "Operación Realizada";
            } else {
                return "No se pudo realizar la operación";
            }
        } catch (SQLException e) {
            return "Error: " + e.toString();
        } catch (Exception e) {
            return "Error: " + e.toString();
        }
    }

    public Bank loadBank(String bankName) {

        String sql_select = "SELECT bank_name, account_number, state, bank_nit " +
                "FROM public.bank WHERE bank_name = ?";
        try {
            System.out.println("Consultando en la base de datos");
            PreparedStatement statement = connection.prepareStatement(sql_select);
            statement.setString(1, bankName);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Bank(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getBoolean(3),
                    resultSet.getString(4)
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
        return new Bank();
    }

    public String[] loadAllBank() {

        ArrayList<String> banks = new ArrayList<>();

        String sql_select = "SELECT bank_name " +
                "FROM public.bank";
        try {
            System.out.println("Consultando en la base de datos");
            PreparedStatement statement = connection.prepareStatement(sql_select);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                banks.add(resultSet.getString(1));
            }
            return banks.toArray(new String[banks.size()]);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
        return new String[]{};
    }

    public String setStateBank(boolean state, String bankNIT) {
        int numRows;
        String sql = "UPDATE bank SET state = ? WHERE bank_nit = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBoolean(1, state);
            preparedStatement.setString(2, bankNIT);
            numRows = preparedStatement.executeUpdate();
            if (numRows > 0) {
                return "Operación Realizada";
            } else {
                return "No se pudo realizar la operación";
            }
        } catch (SQLException e) {
            return "Error: " + e.toString();
        } catch (Exception e) {
            return "Error: " + e.toString();
        }
    }


    public void openDBConnection() {
        connection = dBconnect.getConnection();
    }

    public void closeDBConnection() {
        dBconnect.closeConnection(connection);
    }

}
