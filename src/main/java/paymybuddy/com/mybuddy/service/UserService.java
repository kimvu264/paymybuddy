package paymybuddy.com.mybuddy.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import paymybuddy.com.mybuddy.exception.UserExisted;
import paymybuddy.com.mybuddy.model.Account;
import paymybuddy.com.mybuddy.model.User;
import paymybuddy.com.mybuddy.repository.AccountRepository;
import paymybuddy.com.mybuddy.repository.UserRepository;
import paymybuddy.com.mybuddy.util.AccountUtil;

import java.math.BigDecimal;

@Service
public class UserService {

	private final UserRepository userRepository;

	private final AccountRepository accountRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserService(UserRepository userRepository, AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.accountRepository = accountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	/**
	 * Create user
	 * @param user
	 * @return
	 */
	public User createUser(User user) throws UserExisted {
		if (user.getEmail() != null)
		{
			if(isExistUserByEmail(user)) {
				throw new UserExisted();
			}
			User u = new User();
			u.setEmail(user.getEmail());
			u.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			u.setTel(user.getTel());
			u.setAddress(user.getAddress());
			u.setBirthday(user.getBirthday());
			userRepository.save(u);
			Account account = Account.builder()
					.balance(BigDecimal.ZERO)
					.user(u)
					.accountNumber(AccountUtil.generateNewAccountNumber())
					.build();
			Account savedAccount = accountRepository.save(account);
			u.setAccount(savedAccount);
			u = userRepository.save(u);
			return u;
		}
		return null;
	}

	public boolean isExistUserByEmail( User u) {
		User user = userRepository.findByEmail(u.getEmail());
		if (user != null){
			return true;
		}
		return false;
	}

}
