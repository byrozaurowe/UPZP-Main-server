# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.17

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Disable VCS-based implicit rules.
% : %,v


# Disable VCS-based implicit rules.
% : RCS/%


# Disable VCS-based implicit rules.
% : RCS/%,v


# Disable VCS-based implicit rules.
% : SCCS/s.%


# Disable VCS-based implicit rules.
% : s.%


.SUFFIXES: .hpux_make_needs_suffix_list


# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /snap/clion/139/bin/cmake/linux/bin/cmake

# The command to remove a file.
RM = /snap/clion/139/bin/cmake/linux/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging

# Include any dependencies generated for this target.
include CMakeFiles/client_communication_mocker.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/client_communication_mocker.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/client_communication_mocker.dir/flags.make

CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.o: CMakeFiles/client_communication_mocker.dir/flags.make
CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.o: client_communication_mocker/main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.o -c /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/client_communication_mocker/main.cpp

CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/client_communication_mocker/main.cpp > CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.i

CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/client_communication_mocker/main.cpp -o CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.s

CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.o: CMakeFiles/client_communication_mocker.dir/flags.make
CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.o: UPZP-GameProcess/datagram/datagram.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.o -c /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/UPZP-GameProcess/datagram/datagram.cpp

CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/UPZP-GameProcess/datagram/datagram.cpp > CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.i

CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/UPZP-GameProcess/datagram/datagram.cpp -o CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.s

CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.o: CMakeFiles/client_communication_mocker.dir/flags.make
CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.o: client_communication_mocker/udp_communication_mocker.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.o -c /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/client_communication_mocker/udp_communication_mocker.cpp

CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/client_communication_mocker/udp_communication_mocker.cpp > CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.i

CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/client_communication_mocker/udp_communication_mocker.cpp -o CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.s

# Object files for target client_communication_mocker
client_communication_mocker_OBJECTS = \
"CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.o" \
"CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.o" \
"CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.o"

# External object files for target client_communication_mocker
client_communication_mocker_EXTERNAL_OBJECTS =

client_communication_mocker: CMakeFiles/client_communication_mocker.dir/client_communication_mocker/main.cpp.o
client_communication_mocker: CMakeFiles/client_communication_mocker.dir/UPZP-GameProcess/datagram/datagram.cpp.o
client_communication_mocker: CMakeFiles/client_communication_mocker.dir/client_communication_mocker/udp_communication_mocker.cpp.o
client_communication_mocker: CMakeFiles/client_communication_mocker.dir/build.make
client_communication_mocker: CMakeFiles/client_communication_mocker.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Linking CXX executable client_communication_mocker"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/client_communication_mocker.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/client_communication_mocker.dir/build: client_communication_mocker

.PHONY : CMakeFiles/client_communication_mocker.dir/build

CMakeFiles/client_communication_mocker.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/client_communication_mocker.dir/cmake_clean.cmake
.PHONY : CMakeFiles/client_communication_mocker.dir/clean

CMakeFiles/client_communication_mocker.dir/depend:
	cd /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging /home/winchester/Pulpit/MainServer/UPZP-GameProcess-game_debugging/CMakeFiles/client_communication_mocker.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/client_communication_mocker.dir/depend

