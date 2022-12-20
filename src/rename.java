import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;

class rename {
    private static boolean ifoption(String s) {
        return (s.charAt(0) == '-');
    }

    private static void printhelp() {
        System.out.println("Usage: java rename [-option argument1 argument2 ...]\nOptions:\n-help                   :: display this help and exit. \n-prefix [string]        :: rename the file so that it starts with [string]. \n-suffix [string]        :: rename the file so that it ends with [string]. \n-replace [str1] [str2]  :: rename [filename] by replacing all instances of [str1] with [str2]. \n-file [filename]        :: indicate the [filename] to be modified. );");
    }

    private static String getdate() {
        DateFormat d = new SimpleDateFormat("MM-DD-YYYY");
        Calendar a = Calendar.getInstance();
        return d.format(a.getTime());
    }

    private static String gettime() {
        DateFormat d = new SimpleDateFormat("HH:mm:ss");
        Calendar a = Calendar.getInstance();
        return d.format(a.getTime());
    }

    public static void main(String[] args) {
        int argsnumber = args.length;
        boolean iffile = false;
        boolean ifrule = false;
        boolean iferror = false;
        ArrayList<String> files = new ArrayList<String>();
        //check if there is -help, if there are enough options to execute, and if options and parameters are valid
        for (int i = 0; i < argsnumber; i++) {
            if (ifoption(args[i])) {
                if (args[i].equals("-help")) {
                    printhelp();
                    return;
                } else if (args[i].equals("-file")) {
                    iffile = true;
                    if (i + 1 >= args.length || ifoption(args[i+1])) {
                        System.out.println("Error: Invalid option: file requires one or more parameters");
                        iferror = true;
                    }
                    for(int a = i+1; a < args.length && !ifoption(args[a]); a++) {
                        if (files.contains(args[a])) {
                            System.out.println("Error: '" + args[a] + "' file is repeated");
                        } else files.add(args[a]);
                    }
                } else if (args[i].equals("-prefix")) {
                    if (i + 1 >= args.length || ifoption(args[i+1])) {
                        System.out.println("Error: Invalid option: prefix requires one or more parameters");
                        iferror = true;
                    }
                    ifrule = true;
                } else if (args[i].equals("-suffix")) {
                    if (i + 1 >= args.length || ifoption(args[i+1])) {
                        System.out.println("Error: Invalid option: suffix requires one or more parameters");
                        iferror = true;
                    }
                    ifrule = true;
                } else if (args[i].equals("-replace")) {
                    if (i + 2 >= args.length || ifoption(args[i+1]) || ifoption(args[i+2])) {
                        System.out.println("Error: Invalid option: replace requires exactly 2 parameters");
                        iferror = true;
                    }
                    ifrule = true;
                } else {
                    System.out.println("Error: '" + args[i] + "'is an invalid option. Please supply valid options from the following list:\nOptions:\n-help                   :: display this help and exit. \n-prefix [string]        :: rename the file so that it starts with [string]. \n-suffix [string]        :: rename the file so that it ends with [string]. \n-replace [str1] [str2]  :: rename [filename] by replacing all instances of [str1] with [str2]. \n-file [filename]        :: indicate the [filename] to be modified. );");
                    iferror = true;
                }
            }
        }
        if (!ifrule && !iffile) System.out.println("Error: no options provided");
        else if (!ifrule) System.out.println("Error: no rename rules provided");
        else if (!iffile) System.out.println("Error: no filename provided");
        if (!ifrule || !iffile) {
            printhelp();
            System.out.print(iffile);
            iferror = true;
        }
        if (iferror) return;
        ArrayList<String> result = new ArrayList<String>(files);
        for (int m = 0; m < argsnumber; m++) {
            if (args[m].equals("-prefix") || args[m].equals("-suffix")) {
                ArrayList<String> parameter = new ArrayList<String>();
                for(int a = m + 1; (a < args.length && !ifoption(args[a])); a++) {
                    parameter.add(args[a]);
                }
                if (args[m].equals("-prefix")) {
                    for (int i = 0; i < result.size(); i++) {
                        for (int j = parameter.size() - 1; j >= 0; j--) {
                            if (parameter.get(j).equals("@date")) result.set(i, getdate() + result.get(i));
                            else if (parameter.get(j).equals("@time")) result.set(i, gettime() + result.get(i));
                            else result.set(i, parameter.get(j) + result.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < result.size(); i++) {
                        for (int j = 0; j < parameter.size(); j++) {
                            if (parameter.get(j).equals("@date")) result.set(i, result.get(i) + getdate());
                            else if (parameter.get(j).equals("@time")) result.set(i, result.get(i) + gettime());
                            else result.set(i, result.get(i) + parameter.get(j));
                        }
                    }
                }
            } else if (args[m].equals("-replace")) {
                String source = args[m+1];
                String desti = args[m+2];
                if (desti.equals("@date")) desti = getdate();
                if (desti.equals("@time")) desti = gettime();
                if (source.equals("@date")) source = getdate();
                if (source.equals("@time")) source = gettime();
                for (int i = 0; i < result.size(); i++) {
                    result.set(i, result.get(i).replace(source, desti));
                }
            }
        }
        for (int n = 0; n < files.size(); n++) {
            File file = new File(files.get(n));
            File newfile = new File(result.get(n));
            if (!file.exists()) {
                System.out.println("Error: file '" + files.get(n) + "' does not exist");
            } else if (newfile.exists()) {
                System.out.println("Error: file '" + result.get(n) + "' exists");
            } else {
                file.renameTo(newfile);
                System.out.println("Renaming " + files.get(n) + " to " + result.get(n));
            }
        }
    }
}
