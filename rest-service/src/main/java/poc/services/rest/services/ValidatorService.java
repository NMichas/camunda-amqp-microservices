package poc.services.rest.services;

import org.springframework.stereotype.Service;

@Service
public class ValidatorService {
  public boolean isNameValid(String name) {
    return name.length() > 2;
  }
}
