# Gitlet项目

1.每个commits要存时间/信息/指向blobs的引用，还有指向父亲的指针。

2.要输出错误信息。



先搞清楚.git中文件的结构。对每个操作都用类表示，在Repository中调用对应类中的操作。

内部数据结构实现: 在.gitlet文件夹中建一个表示暂存区的文件夹stage,再建一个文件夹blobs表示commit的文件,blobs里面放已经提交上去的文件，文件名用sha1来命名。在refs文件夹中存对应的commit信息。每个文件还是以对应的sha1来命名。工作目录中存HEAD文件来记录现在HEAD指针指向的commit的sha1.





commit操作:

首先定义一个commit类，外层类主要负责记录SHA1，内层类中有一个Set存对应的blobs的hash值，还有一些其他必要记录的信息。



1.Repository类中先读取当前HEAD指向的文件位置。可以得到一个parent commit.

2.新创建一个commit，从parent commit中得到（这里可以直接readobject而不是直接用赋值，怕赋值会对parent commit有影响）

3.扫描cache文件夹中的文件(不应该直接在文件夹中扫描，而是把文件夹中所有的文件名都存到一个set中，每次load的时候就读入这个set来搞)，将所有的文件放到commit中的set中。

4.清空cache文件夹。

5.更新Repository中map,保存了所有commit的map<String,File>，即SHA1为某个String对应的File

6.Repository中还要存一个map<String,File>来保存分支名为String的commit.

7.利用java中的时间函数更新commit的时间信息，存储在commit的子类中，以及用户提交的commit message

8.最后把子类做一个SHA1,存起来。



update: 所有这些存储的信息应该建一个config类，每次启动程序的时候load,并通过这个类来写入。

update2:caches不能存file,因为可能要踢出上一个版本



initialcommit:

1.创一个master分支

2.初始化HEAD

3.创建commit,并保存为文件



checkout:

1.先得到一个Commit实例，记得处理错误信息

2.在Commit实例中查找对应的blob名字,注意这里contain的写法，用equals

3.在BLOB文件夹中复制出来放到工作目录中



rm:

1.要对整个项目结构进行大改,在cache中要建两个子文件夹，分别为add_cache和remove_cache

2.对add和commit都要进行改动

3.如果文件在当前的commit中，把这个文件放入removecaches中，并且在工作目录中删除这个文件

4.接下来改commit,那么又有一个问题：如果在addcache和removecache中都存在一个相同的文件该如何commit呢？



branch:

1.这个命令一开始只是简单的创建一个指针

2.需要继续写checkout和commit(不知道之前有没有更新过branch)

nowbranch存的就是当前的一个branch的名字罢了



checkout-update:

如果要从其他分支中拿出一个文件并**覆盖**工作目录中的同名文件，一定要先检查这个文件是否包含在最近的commit中



checkout branch:

1.先检查对应的分支是否存在,错误处理

2.再检查nowbranch是不是要checkout的分支，错误处理

3.遍历branch所指commit的所有文件和当前commit的所有文件，看有没有会overwrite的unstaged的文件，错误处理

4.更改HEAD指针和nowbranch，把工作区的文件全部删光，然后把当前branch指向的commit的文件全部拿出来放到工作区

5.清空addcache和removecache



那么接下来要做reset和status了，做完这两个就要检验checkout branch,reset和status. 最后做merge



status:只用看当前的commit，需要先学怎么给字符串排序！

倒数第二类的处理：

1.在当前的commit中，并且也在工作目录中，但文件内容发生了改变，并且没有stage

2.已经stage for addition了，但是不在工作区中或者和工作区中的文件内容不同

3.在当前的commit中，但是不在工作目录中并且也不在stage for remove中



第一类和第三类通过扫描commit的blobs来处理

第二类扫描addcaches来处理



测试branch,checkout branch,status:

java gitlet.Main init

java gitlet.Main add wug.txt

java gitlet.Main status

java gitlet.Main commit 1

java gitlet.Main status

java gitlet.Main branch zbtrs

java gitlet.Main status

java gitlet.Main add wug2.txt

java gitlet.Main status

java gitlet.Main commit 2

java gitlet.Main status

java gitlet.Main checkout zbtrs

java gitlet.Main status

改变wug.txt

java gitlet.Main status

创建wug2.txt

java gitlet.Main status

java gitlet.Main add wug2.txt

java gitlet.Main status

改变wug2.txt

java gitlet.Main status

删除wug.txt

java gitlet.Main status

删除wug2.txt

java gitlet.Main status

md自己测有dangeous的操作，不好搞，等下学一下怎么搞test



Merge:

1.是把给定的分支名的commit合并到当前的分支上，是合并过来

2.先要找两个commit的最近公共祖先，如果最近公共祖先是给定的这个分支，那么这个操作就说明要把老的合并到新的上面，直接输出：Given branch is an ancestor of the current branch.然后结束. **如果最近公共祖先是当前分支，那么就直接checkout given branch,输出:Current branch fast-forwarded.后结束**

3.对于这样一些文件：在给定的分支中和最近公共祖先的commit中内容不一样，在当前分支中和最近公共祖先的commit中内容相同，把这些文件给stage起来

4.如果在当前分支中更改了，但是在给定分支中没有更改，就不变

5.如果一个文件在两个分支中进行了相同的修改，比如都和LCA不同但是两个分支中的文件内容相同，或者都删除了，则这个文件在合并中不进行任何操作

6.文件不在LCA也不在给定分支，但在当前分支中，不变

**7.文件不在LCA也不在当前分支中，但在给定分支中，则这个文件应该被checkout并且stage**

8.在LCA，并且也在当前分支中没有修改，但是不在给定分支中的文件应该被删除和untracked,也就是只动commit的这个文件而不动工作区的这个文件

9.在LCA，并且在给定分支中没有修改，但是不在当前分支中的文件应该继续保持不存在

10.如果一个文件满足下列任意特征之一：

LCA中有文件，两个分支中文件内容都跟LCA不同并且彼此也不相同

**LCA中有文件，其中一个分支的文件内容改变了，另外一个分支的文件被删掉了**

LCA中没有这个文件，并且两个分支中的这个文件有不同的内容

将这个文件替换成：

```
<<<<<<< HEAD
contents of file in current branch
=======
contents of file in given branch
>>>>>>>
```

并且把这个文件暂存起来

11.所有操作完成后进行一次commit,commitmessage为Merged [given branch name] into [current branch name]. 如果这次merge有冲突，就在终端上打印Encountered a merge conflict.



如果addcaches或者removecaches里面还有文件，错误信息You have uncommitted changes.

如果给定的分支名不存在，错误：A branch with that name does not exist.

如果尝试merge两个相同的分支：Cannot merge a branch with itself.

如果检查的上面的所有文件，合并两个分支没有任何改变，输出普通commit没有改变的错误信息

如果有一个文件在当前的commit中untrack并且在这次合并中会被重写或者删除，错误：There is an untracked file in the way; delete it, or add and commit it first.



所以具体步骤:

1.判定前3个错误.

2.找到最近公共祖先，这里可能会用到算法,O(N)

3.遍历工作目录，找到untracked的文件。对上述三种情形进行判断

4.把LCA、目标分支、当前分支中的文件全部提取出来，放到一个set中，依次进行上述判断。

5.用一个boolean变量记录合并后的分支到底是否会有所改变



怎么找LCA?

因为每个点可能会有两个parent,所以需要进行BFS. 为两个分支各开一个map记录分支和距离commit的距离，这个BFS要用到队列来进行。同时要维护两个set。接下来在一个分支的set中遍历，找到另外一个分支的map中最小的value的那个commit,就是LCA.



对于commit，还应该记录一个secondparent,改变一下initial commit和commit的构造函数

对于log也需要更改。

先改commit和log



## 项目规范

Config.commit2file: Map<String,File>,存储commit的SHA1-->具体的commit所存储的文件

Config.branch2commit: Map<String,File>,分支名为给定字符串对应的commit的文件

Config.caches:暂存区中所有的文件的**文件名**

Config.HEADfile:是一个文本文件，存HEAD的文本

Config.commits:存commit2file

Config.branchs:存branch2commit

Config.cachesfile:存caches

Config.nowbranch:存现在的branch.

Config.HEAD:存HEAD对应的commit的SHA1.

所有的目录都放在Repository.java中



BLOBS_DIR中存的blob都是文本文件！只是把blob中的文本内容写入到了文件中，并没有把整个blob结构体写入文件！



对于git的每个操作，都必须在开始前config.load(),结束后config.store()



update:发现暂存区中的同名文件只能存一份，那么暂存区中的文件就不能用SHA1来寻址而应该用文件名

update:还有一个问题:在add中涉及往cache中写入文件，应该写入的是文本文件！！！！

因为Blob的构造函数就是读入文本内容！

update:大问题：没有仔细阅读和测试Utils中SHA1函数，如果要得到一个结构体的SHA1,最好的方法是采用递归的方法，即大的结构体通过其中子成员的SHA1来得到

update:一个疏忽：iscontain必须要手写比较函数！

每次重新编译整个项目都会给实现了序列化interface的类一个序列化id,如果id不同则不能读写一份文件



## 总结

1.还是应该通读整个需求，不要一上来就直接开始写

2.先想好要定义几个类，可能会用到哪些方法或常量，放在哪些类里面

3.在写一个类的时候把最常见的接口先放上去，时刻写注释和文档

4.在写一个类的方法之前，要先想好注意事项和顺序，尤其是文件的读写！

5.每写一个功能一定要调试！单元调试，不要积压到一起

6.命名问题，一定要详细，并且在写项目之前就要制定好一个规范



