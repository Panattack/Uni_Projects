from cProfile import label
from distutils import extension
from tkinter import *
import shutil         
import os
from turtle import bgcolor
import easygui
from tkinter import filedialog
from tkinter import messagebox as mb

def changeOnHover(button,HoverColor,LeftColor):
    button.bind("<Enter>", func=lambda e: button.config(
        background=HoverColor))
    button.bind("<Leave>", func=lambda e: button.config(
        background=LeftColor))

def raiseErrorinX(ob):
    if ob is None:
        raise Exception()

class FileManager:
    #open a file box window
    #when we want to select a file
    def open_window(self):
        read = easygui.fileopenbox()
        return read

    # open a file function
    def open_file(self):
        selected_file = self.open_window()
        try:
            os.startfile(selected_file)
        except:
            mb.showerror(selected_file,'File not found')

    # open file function
    def copy_file(self):
        try:
            copied_file = self.open_window()
            raiseErrorinX(copied_file)
            destination = filedialog.askdirectory()
            raiseErrorinX(destination)
            shutil.copy(copied_file,destination)
            mb.showinfo('confirmation','File Copied!')
        except:
            mb.showerror(message='No file copied')

    # delete file
    def delete_file(self):
        del_file = self.open_window()
        if os.path.exists(del_file):
            os.remove(del_file)
        else:
            mb.showinfo(del_file,"File not found !")

    # Rename a file
    def rename_file(self):
        chosen_file = self.open_window()
        # returns the directory name of the pathname path
        first_path = os.path.dirname(chosen_file)
        # Split a path in root and extension.
        # The extension is everything starting at the last dot in the last
        # pathname component; the root is everything before that.
        # It is always true that root + ext == p.
        extension = os.path.splitext(chosen_file)[1]
        print("Enter a new name for the chosen file")
        new_Name = input()
        path = os.path.join(first_path,new_Name+extension)
        print(path)
        os.rename(chosen_file,path)
        mb.showinfo('confirmation','File Rename !')

    # Moving a file
    def move_file(self):
        source = self.open_window()
        destination = filedialog.askdirectory()
        if (source == destination):
            mb.showinfo('confirmation',"Source and Destination are the same")
        else:
            shutil.move(source,destination)
            mb.showinfo('confirmation',"File Moved")

    # Making a folder
    def make_a_folder(self):
        new_folder_path = filedialog.askdirectory()
        print("Enter name of new folder")

        newfolder = input()
        path = os.path.join(new_folder_path,newfolder)

        os.mkdir(path=path)

        mb.showinfo('confirmation',"Folder Created !")

    def remove_a_folder(self):
        delfolder = filedialog.askdirectory()
        os.rmdir(delfolder)
        mb.showinfo('confirmation',"Folder Deleted !")

    # Listing files and sorting them in an alphabetical order
    def listfiles_in_a_folder(self):
        folder = filedialog.askdirectory()
        sortlist = sorted(os.listdir(folder))
        print(f'The files insider the {folder} folder are:')
        for file in sortlist:
            print(file,'\n')

if __name__ == '__main__':
    fm = FileManager()
    root = Tk(className='File Manager')
    root.configure(bg='black')
    # canvas = Canvas(root,width=500,height=100,bg='black')
    # canvas.grid(row=0, column=2)
    l1 = Label(root,text='Choose an option',font=("Times", "16", "bold italic"), fg="blue", bg='black').grid(row = 0, column = 1)
    b1 = Button(root,text="Open a file",command=fm.open_file)
    b1.grid(row=1,column=1)
    b2 = Button(root, text = "Copy a File", command = fm.copy_file)
    b2.grid(row = 1, column = 2)
    b3 = Button(root, text = "Delete a File", command = fm.delete_file)
    b3.grid(row = 5, column = 1)
    b4 = Button(root, text = "Rename a File", command = fm.rename_file)
    b4.grid(row = 5, column = 2)
    b5 = Button(root, text = "Move a File", command = fm.move_file)
    b5.grid(row = 9, column = 1)
    b6 = Button(root, text = "Make a Folder", command = fm.make_a_folder)
    b6.grid(row = 9, column = 2)
    b7 = Button(root, text = "Remove a Folder", command = fm.remove_a_folder)
    b7.grid(row = 13, column = 1)
    b8 = Button(root, text = "List all Files in Directory", command = fm.listfiles_in_a_folder)
    b8.grid(row = 13, column = 2)
    ls = [b1,b2,b3,b4,b5,b6,b7,b8]
    for i in ls:
        changeOnHover(i,"yellow","white")
    root.mainloop()