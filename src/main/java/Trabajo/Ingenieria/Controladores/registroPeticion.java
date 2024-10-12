package Trabajo.Ingenieria.Controladores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class registroPeticion {
    String nombre;
    String username;
    String correo;
    String celular;
    String password;
}
