# simpleShell-java
a simple shell in Java using processBuilder to run almost all CMD/Terminal Commands. Like "cd", "dir", "type", "ping" or any kind of command that don't need answer.


# features
## Support command-history.

To access history enter "history" as command.

to run last command enter "!!"

to run command-history number i, enter "!i". (e.g !3)

## Changing Directory

just use cd command as you use in cmd/terminal

both releative and absolute addressing is supported

to change directory to a folder with a space-included name (e.g power point) you should put the folder name in double quotation. (like this: cd "power point". This is because space is delimeter by default.

this code could handle invalid directories.
