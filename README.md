![steelbrew](https://github.com/rwiuff/02125Thesis/blob/main/graphics/steelbrew.png)
# SteelBrew
This reporsitory contains SteelBrew, the Bachelor's project by Rasmus Wiuff ([rwiuff@gmail.com](mailto:rwiuff@gmail.com)).

The related thesis can be found in the repository [rwiuff/02125Thesis](https://github.com/rwiuff/02125Thesis)
## Contents
1. [Requirements](#requirements)
2. [Download and usage](#download-and-usage)
3. [SteelBrew commands](#steelbrew-commands)
4. [Todo list](#todo-list)
## Requirements
Verilator, GNU Make and gcc is required to run the software.

If on Windows, install and enable Ubuntu on WSL.

A guide can be found [here](https://learn.microsoft.com/en-us/windows/wsl/install).

For installing Verilator follow the guide [here](https://veripool.org/guide/latest/install.html).
## Download and usage
As of now the project is run by downloading the repo:
```
git clone https://github.com/rwiuff/SteelBrew.git
```
To play around with the demo do as follows:
1. Open the cloned repo in your favourite [Gradle](https://gradle.org/) enabled editor.
2. Go to the file
   >app/src/main/java/org/rwiuff/steelbrew/Driver.java
3. Edit the driver and run the java application.
## SteelBrew commands
### Initialise the testing environment
Start the tests by creating a SteelBrew object as such:
```
SteelBrew steelBrew = new SteelBrew();
```
Then if on Windows, enable WSL:
```
Forge.enableWSL(true);
```
### Adding a device:
Devices can easily be added with the following:
```
Brewer dut = new Brewer("dut");
```
It is important that the string given to the `Brewer` constructor matches the filename of the `.sv` file containing the device under test SystemVerilog code.

To set the number of clockcycles:
```
dut.clocks(n);
```
with `n` being some integer.

The signals used in the tests are declared using the `Signal` object:
```
Signal in = new Signal("in_valid", 1);
```
where the signal name matches the signal in the device code. The `1` is the signal width and is for later versions of SteelBrew.
### Defining tests
The test scenarios are created using a `Batch`:
```
Batch batch = new Batch("Tests");
```
The use the following commands to carry out tests:
- `peek` to see the value of a previously defined signal
  ```
  batch.peek(signal);
  ```
- `poke` to change the value of a previously defined signal
  ```
  batch.poke(signal, BigInteger.ONE);
  ```
- `step` to go to the next clock cycle
  ```
  batch.step();
  ```
- `expect` to raise an alarm if some signal does not match a given value
  ```
  batch.expect(signal, BigInteger.ONE);
  ```
### Running simulations
Once the tests are defined, add them to the DUT:
```
dut.brew(batch);
```
Then, run the simulations:
```
Forge.simulate();
```
### Auxilary commands
From a declared SteelBrew object, there are commands to clean the working directory:
- `.cleanAux()` for removing the testbenches, waveforms and stamps.
- `.dleanObj()` for removing the `obj_dir`
- `.clean()` for executing both of the above
## Todo list
- [X] Clean up the STDOUT for a nicer user experience.
- [X] Rewrite testbench heuristics to allow for time-independent tests.
- [X] Implement assertions
- [ ] Implement ABV as per SVA
- [ ] Enable Verilator multithreading
- [ ] Refactor into deployable plugin
