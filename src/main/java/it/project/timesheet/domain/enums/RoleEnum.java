package it.project.timesheet.domain.enums;

public enum RoleEnum {
    USER("USER"),
    ADMIN("ADMIN");

    private final String roleName;

    // Costruttore privato
    RoleEnum(String roleName) {
        this.roleName = roleName;
    }

    // Metodo per ottenere il valore stringa dell'enum
    public String getRoleName() {
        return roleName;
    }

    // Metodo per convertire da stringa a enum
    public static RoleEnum fromString(String roleName) {
        if (roleName != null) {
            for (RoleEnum role : RoleEnum.values()) {
                if (role.roleName.equalsIgnoreCase(roleName)) {
                    return role;
                }
            }
        }
        throw new IllegalArgumentException("Ruolo non valido: " + roleName);
    }

    // Metodo per convertire enum in stringa
    @Override
    public String toString() {
        return this.roleName;
    }
}
