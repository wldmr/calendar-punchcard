# Requires the android tools and platform-tools to be in the PATH
# Use ". bashrc" for that.

install:
	ant -emacs debug install

debug:
	ant -emacs debug

emulator:
	emulator @GalaxyS2 -timezone "Europe/Berlin" -gpu on -qemu -enable-kvm &
