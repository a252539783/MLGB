cmake_minimum_required(VERSION 3.4.1) #版本号，使用默认值（默认值应该是从模板创建时默认的参数吧）或者3.4.0以下数值

add_library(               #添加编译目标（即.so）
             aaaa          #编译目标名称(不要指定为libxxx.so,名称中平台相关参数会自动添加)
             SHARED        #SHARED 或者 STATIC  ，默认SHARED   （共享/静态链接库）
             empty.c;hook.c )    #源代码文件路径，相对路径（注意是从CMakeLists.txt所在路径的相对路径，如果CMakeLists.txt位于app主目录下，则此处应为./src/main/cpp/aaaa.cpp;若CMakeLists.txt位于cpp目录下，则为aaaa.cpp）

find_library(            #寻找外部依赖库，并定义变量引用
              log-lib    #将外部依赖库定义为变量，即此处名称任意，只要自己知道就好
              log )      #外部依赖库的名称，同add_library的命名规则，不要添加任何平台相关参数

target_link_libraries(                  #链接（此处为将aaaa链接到log-lib,使aaaa可以调用log-lib中的内容）
                       aaaa             #需要被链接的目标
                       ${log-lib} )