public class UserInputOld {

    public boolean validate(String userInput){
        ValidatorOld validator = new ValidatorOld();
        return validator.validate(userInput);
    }
}
