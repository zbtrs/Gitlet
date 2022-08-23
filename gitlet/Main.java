package gitlet;

import java.util.Date;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    private static void test() {
        Test temp = new Test();
        temp.test();
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }

        Repository object = new Repository();
        //object.add("test2.txt");

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.init();
                break;
            case "add":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.add(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.remove(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.commit(args[1],new Date(),0,"");
                break;
            case "log":
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.log();
                break;
            case "global-log":
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.globallog();
                break;
            case "checkout":
                if (args.length <= 1 || args.length > 4) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        Utils.message("Incorrect operands.");
                        System.exit(0);
                    }
                    object.checkoutfile("current file",args[2],0);
                }

                if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        Utils.message("Incorrect operands.");
                        System.exit(0);
                    }
                    object.checkoutfile(args[1],args[3],1);
                }
                if (args.length == 2) {
                    object.checkoutbranch(args[1],0);
                }
                break;
            case "find":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.find(args[1]);
                break;
            case "branch":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.branch(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.removebranch(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.status();
                break;
            case "reset":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.reset(args[1]);
                break;
            case "merge":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.merge(args[1]);
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
                break;
        }
    }

}

