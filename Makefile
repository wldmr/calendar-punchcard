# Requires the android tools and platform-tools to be in the PATH
# Use ". bashrc" for that.

debug_install:
	ant debug install

emulator:
	emulator @GalaxyS2 -gpu on -qemu -enable-kvm &
