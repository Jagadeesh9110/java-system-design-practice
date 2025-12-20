package library;

import java.util.Map;
import java.util.HashMap;

public class BorrowPolicyFactory {
     private final Map<UserType, BorrowPolicy> policyMap;

     public BorrowPolicyFactory() {
         policyMap = new HashMap<>();
         policyMap.put(UserType.STUDENT, new StudentBorrowPolicy());
         policyMap.put(UserType.FACULTY, new FacultyBorrowPolicy());
     }

     public BorrowPolicy getPolicy(User user){
        if(user==null){
            throw new IllegalArgumentException("User cannot be null");

        }
        BorrowPolicy policy= policyMap.get(user.getUserType());
        if(policy==null){
            throw new IllegalStateException("No BorrowPolicy found for UserType: " + user.getUserType());
        }

        return policy;
     }
}
