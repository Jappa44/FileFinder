# FileFinder
A program to read through a users file tree starting from a given parent folder and finding all subfolders. 
It then ranks these subfolders by their size in GB.  May eventually expand this to a GUI application.

The purpose of this program is to find all of the subfolders of a given folder and to then rank them
by their size in GB. The specified folder must be specified using its absolute path, that is, including
all parent folders up to the drive itself. For example, C:\, C:\Program Files\java\ or D:\downloads\ are all 
absolute paths. The process of finding all subfolders, their size, and then sorting them will take 
significant time for very large folders, like the entire C:\ drive for example. The program will print
when it finds a new folder just so that you can see it's doing something. The program will also print
every time the size of two folders is compared during the sorting process for the same reason. There is
no way to know how long the entire process will take, but an estimate from checking the entirety of a 
nearly full 2TB drive is around 20 minutes. 
