import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SimpleShell {
    public static void main(String[] args) {
        CommandHistory ch = new CommandHistory();
        String current_path = System.getProperty("user.dir");
        File dir = new File(current_path);
        while (true) {
            pr(current_path + ">");
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();
            if (line.equals(""))
                continue;
            if (line.equals("exit") || line.equals("quit")) {
                prl("Goodbye");
                System.exit(0);
            }

            ArrayList<String> command_parts = new ArrayList<String>(5);
            String main_operation = prepare_command(line, command_parts);

            if (main_operation.equals("history")) {
                prl(ch.get_history());
                continue;
            }
            if (main_operation.startsWith("!")) {
                if (main_operation.equals("!!")) {//repeat last command
                    String last_command = ch.last_command();
                    if (last_command == null) {
                        prl("no command entered yet!");
                        continue;
                    } else {
                        command_parts = new ArrayList<String>(5);
                        main_operation = prepare_command(last_command, command_parts);
                    }
                } else {//run command number i
                    try {
                        int i = Integer.parseInt(main_operation.substring(1));
                        String pre_command = ch.get_command(i);
                        if (pre_command == null) {
                            prl("Wrong command number!");
                            continue;
                        } else {
                            command_parts = new ArrayList<String>(5);
                            main_operation = prepare_command(pre_command, command_parts);
                        }
                    } catch (Exception e) {
                        prl("invalid command. Hint: enter !+number to get your command.");
                        continue;
                    }
                }
            } else {
                ch.add_command(line);
            }

            if (main_operation.equals("cd") || main_operation.equals("chdir")) {
                if (command_parts.size() < 4) {
                    prl("invalid cd command! hint: you need to provide new directory");
                    continue;
                }

                String new_path = command_parts.get(3).replaceAll("^\"|\"$", "");
                Path p = Paths.get(new_path);
                File new_dir;
                if (p.isAbsolute())
                    new_dir = new File(new_path);
                else
                    new_dir = new File(dir, new_path);

                if (!new_dir.exists() || !new_dir.isDirectory()) {
                    prl("Error: invalid directory requested");
                    continue;
                } else {
                    dir = new_dir;
                }

            }
            try {
                ProcessBuilder pb = new ProcessBuilder(command_parts);
                pb.directory(dir);
                Process process = pb.start();
                // obtain the input stream
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String ln;
                while ((ln = br.readLine()) != null)
                    prl(ln);
                br.close();

                current_path = dir.getCanonicalPath();
            } catch (java.io.IOException e) {
                prl(e.getMessage());
            }
        }//end of while
    }//end of main method

    private static String prepare_command(String command_line, ArrayList<String> str_list) {
        String[] str_arr = command_line.split(" ");
        str_list.add("cmd");
        str_list.add("/c");
        boolean quotation = false;//enable support for passing argument with space included
        for (int i = 0; i < str_arr.length; i++) {
            String str = str_arr[i];
            if (quotation) {
                int last_index = str_list.size() - 1;
                str_list.set(last_index, str_list.get(last_index) + " " + str);
            } else
                str_list.add(str);
            if (str_arr[i].startsWith("\""))
                quotation = true;
            if (str_arr[i].endsWith("\""))
                quotation = false;
        }
        return str_list.get(2);
    }

    public static void pr(String message) {
        System.out.print(message);
    }

    public static void prl(String message) {
        System.out.println(message);
    }
}//end of class

class CommandHistory {
    private ArrayList<String> list;
    private int n;

    public CommandHistory() {
        list = new ArrayList<String>(10);
        n = 0;
    }

    public String get_history() {
        if (!list.isEmpty()) {
            String out = "here is a list of your commands:\nEnter !<number> to get your command.\n";
            int i = 1;
            for (String command : list) {
                out += i + ") " + command + "\n";
                i++;
            }
            return out;
        }
        return "no history available!";
    }

    public void add_command(String cmd) {
        list.add(cmd);
        n++;
    }

    public String last_command() {
        if (!list.isEmpty())
            return list.get(n - 1);
        return null;
    }

    public String get_command(int number) {
        if (number > 0 && number <= n)
            return list.get(number - 1);
        return null;
    }
}