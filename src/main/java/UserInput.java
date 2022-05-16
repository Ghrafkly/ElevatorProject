public class UserInput {


    public boolean validate(String userInput){

        Validator validator = new Validator();
        return validator.validate(userInput);

    }

//    public void execute(String command){
//
//        if (validate(command)){
//            //RUN COMMAND
//        }
//        else {
//            //IMPLEMENT ERROR MESSAGE
//        }
//
//    }

}
