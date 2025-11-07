package com.training.dunningcuring.config;

import com.training.dunningcuring.auth.entity.ERole;
import com.training.dunningcuring.auth.entity.Role;
import com.training.dunningcuring.auth.entity.User;
import com.training.dunningcuring.auth.repository.RoleRepository;
import com.training.dunningcuring.auth.repository.UserRepository;
import com.training.dunningcuring.customer.entity.Customer;
import com.training.dunningcuring.customer.entity.CustomerSegment;
import com.training.dunningcuring.customer.entity.ServiceStatus;
import com.training.dunningcuring.customer.repository.CustomerRepository;
import com.training.dunningcuring.dunning.entity.DunningAction;
import com.training.dunningcuring.dunning.entity.DunningRule;
import com.training.dunningcuring.dunning.repository.DunningRuleRepository;
import com.training.dunningcuring.plan.entity.Plan;
import com.training.dunningcuring.plan.entity.ServiceType;
import com.training.dunningcuring.plan.entity.Subscription;
import com.training.dunningcuring.plan.repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final DunningRuleRepository dunningRuleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlanRepository planRepository;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository,
                      CustomerRepository customerRepository, DunningRuleRepository dunningRuleRepository,
                      PasswordEncoder passwordEncoder, PlanRepository planRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.dunningRuleRepository = dunningRuleRepository;
        this.passwordEncoder = passwordEncoder;
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Step 1: Create Roles
        Role adminRole = loadRole(ERole.ROLE_ADMIN);
        // Role supportRole = loadRole(ERole.ROLE_SUPPORT_AGENT);
        Role customerRole = loadRole(ERole.ROLE_CUSTOMER);

        // Step 2: Create Admin & Support Users
        if (userRepository.findByUsername("admin@dcms.com").isEmpty()) {
            User adminUser = new User("admin@dcms.com", passwordEncoder.encode("admin123"));
            adminUser.setRoles(Set.of(adminRole));
            userRepository.save(adminUser);
        }
        /*if (userRepository.findByUsername("support@dcms.com").isEmpty()) {
            User supportUser = new User("support@dcms.com", passwordEncoder.encode("support123"));
            supportUser.setRoles(Set.of(supportRole));
            userRepository.save(supportUser);
        }*/

        // --- STEP 3: CREATE SERVICE PLANS (YOUR 6 PLANS) ---
        if (planRepository.count() == 0) {
            planRepository.save(new Plan("Net50", "50 mbps", new BigDecimal("29.00"), ServiceType.BROADBAND, CustomerSegment.PREPAID, 5000));
            planRepository.save(new Plan("Net100", "100 mbps", new BigDecimal("59.00"), ServiceType.BROADBAND, CustomerSegment.PREPAID, 10000));
            planRepository.save(new Plan("FibreNet", "100 mbps", new BigDecimal("199.00"), ServiceType.BROADBAND, CustomerSegment.POSTPAID, 0));
            planRepository.save(new Plan("Net500", "500 mbps", new BigDecimal("299.00"), ServiceType.BROADBAND, CustomerSegment.POSTPAID, 0));
            planRepository.save(new Plan("Unlimited 5G", "Premium 5G", new BigDecimal("499.00"), ServiceType.MOBILE, CustomerSegment.POSTPAID, 0));
            planRepository.save(new Plan("MobilePrepaid-10GB", "10GB Data", new BigDecimal("149.00"), ServiceType.MOBILE, CustomerSegment.PREPAID, 10240));
            System.out.println(">>> Created 6 Default Plans");
        }
        // --- END OF STEP 3 ---

        // Step 4: Create Ramesh (POSTPAID)
        if (customerRepository.findByEmail("ramesh@test.com").isEmpty()) {
            // createCustomer helper already sets status to INACTIVE and balance to 0
            Customer ramesh = createCustomer("Ramesh", "Kumar", "ramesh@test.com", "9876543210", CustomerSegment.POSTPAID);

            if (userRepository.findByUsername("ramesh@test.com").isEmpty()) {
                User customerUser = new User("ramesh@test.com", passwordEncoder.encode("ramesh123"));
                customerUser.setRoles(Set.of(customerRole));
                customerUser.setCustomerProfile(ramesh);
                ramesh.setUser(customerUser);
                userRepository.save(customerUser);
            }
        }

        // Step 5: Create Priya (PREPAID)
        if (customerRepository.findByEmail("priya@test.com").isEmpty()) {
            // createCustomer helper already sets status to INACTIVE and balance to 0
            Customer priya = createCustomer("Priya", "Sharma", "priya@test.com", "9123456789", CustomerSegment.PREPAID);

            if (userRepository.findByUsername("priya@test.com").isEmpty()) {
                User customerUser = new User("priya@test.com", passwordEncoder.encode("priya123"));
                customerUser.setRoles(Set.of(customerRole));
                customerUser.setCustomerProfile(priya);
                priya.setUser(customerUser);
                userRepository.save(customerUser);
            }
        }

        // Step 6: Create 3 more test customers (INACTIVE)
        if (customerRepository.findByEmail("david@test.com").isEmpty()) {
            Customer david = createCustomer("David", "Lee", "david@test.com", "9234567890", CustomerSegment.POSTPAID);
            if (userRepository.findByUsername("david@test.com").isEmpty()) {
                User customerUser = new User("david@test.com", passwordEncoder.encode("david123"));
                customerUser.setRoles(Set.of(customerRole));
                customerUser.setCustomerProfile(david);
                david.setUser(customerUser);
                userRepository.save(customerUser);
            }
        }
        if (customerRepository.findByEmail("sarah@test.com").isEmpty()) {
            Customer sarah = createCustomer("Sarah", "Chen", "sarah@test.com", "9345678901", CustomerSegment.PREPAID);
            if (userRepository.findByUsername("sarah@test.com").isEmpty()) {
                User customerUser = new User("sarah@test.com", passwordEncoder.encode("sarah123"));
                customerUser.setRoles(Set.of(customerRole));
                customerUser.setCustomerProfile(sarah);
                sarah.setUser(customerUser);
                userRepository.save(customerUser);
            }
        }
        if (customerRepository.findByEmail("mike@test.com").isEmpty()) {
            Customer mike = createCustomer("Mike", "Brown", "mike@test.com", "9456789012", CustomerSegment.POSTPAID);
            if (userRepository.findByUsername("mike@test.com").isEmpty()) {
                User customerUser = new User("mike@test.com", passwordEncoder.encode("mike123"));
                customerUser.setRoles(Set.of(customerRole));
                customerUser.setCustomerProfile(mike);
                mike.setUser(customerUser);
                userRepository.save(customerUser);
            }
        }

        // Step 7: Create Dunning Rules
        if (dunningRuleRepository.count() == 0) {
            DunningRule rule1 = new DunningRule();
            rule1.setRuleName("Rule 1: Day 3 Warning");
            rule1.setActionToTake(DunningAction.SEND_EMAIL);
            rule1.setTargetSegment(CustomerSegment.ALL);
            rule1.setMinOverdueDays(1);
            rule1.setMaxOverdueDays(4);
            rule1.setActive(true);
            dunningRuleRepository.save(rule1);

            DunningRule rule2 = new DunningRule();
            rule2.setRuleName("Rule 2: Day 5 Throttle");
            rule2.setActionToTake(DunningAction.THROTTLE_DATA);
            rule2.setTargetSegment(CustomerSegment.POSTPAID);
            rule2.setMinOverdueDays(5);
            rule2.setMaxOverdueDays(7);
            rule2.setActive(true);
            dunningRuleRepository.save(rule2);

            DunningRule rule3 = new DunningRule();
            rule3.setRuleName("Rule 3: Day 8 Block");
            rule3.setActionToTake(DunningAction.BLOCK_ALL_SERVICES);
            rule3.setTargetSegment(CustomerSegment.POSTPAID);
            rule3.setMinOverdueDays(8);
            rule3.setMaxOverdueDays(999);
            rule3.setActive(true);
            dunningRuleRepository.save(rule3);
        }
    }

    private Role loadRole(ERole roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    // Helper method to create a customer
    private Customer createCustomer(String first, String last, String email, String phone, CustomerSegment segment) {
        Customer customer = new Customer();
        customer.setFirstName(first);
        customer.setLastName(last);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setSegment(segment);
        customer.setStatus(ServiceStatus.INACTIVE); // Default to INACTIVE
        customer.setBalance(BigDecimal.ZERO); // Default to 0.00
        customer.setAmountOverdue(BigDecimal.ZERO);
        customer.setOverdueDays(0);
        return customer;
    }
}
