package utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    public static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("UniManagementPU");

}