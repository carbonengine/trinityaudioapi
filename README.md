# Carbon Trinity-Audio Interfaces
Single source of truth for audio interfaces shared between Carbon Trinity and Carbon Audio systems. This repository centralizes interface definitions to prevent API inconsistencies and maintain compatibility between systems.

# Current Interfaces

- `include/ITr2AudEmitter.h` - Interface for audio emitters
- `include/ITr2Audio.h` - Interface for audio functionality on assets
- `include/IAudioInputMgr.h` - Interface for audio input management

# Usage
To use this library in your project, add the following to your CMakeLists.txt:

```cmake
find_package(CarbonTrinityAudioAPI REQUIRED)
target_link_libraries(YourTarget INTERFACE CarbonTrinityAudioAPI)
```

# Building the default project Locally

1. run after cloning this repository, or any carbon-component, `cd` into it and run `git submodule update --init` 
2. At the top level of your project (alongside your CMakeLists.txt file) create a file called: `CMakeUserPresets.json`
3. Copy the contents of the `CMakePresets.json` file into `CMakeUserPresets.json` and delete everything from the `configurePresets` array
4. Add a new preset into the `configurePresets` array:

```json
{
  "name": "local",
  // inherit all of the settings from this preset in CMakePresets.json
  "inherits": "carbon_windows_vcpkg_vs",
  "environment": {
    // put any other environment variables that you want here
    "CCP_EVE_PERFORCE_BRANCH_PATH": "D:/perforce/eve-frontier/branches/sandbox/VCPKG-MIGRATION"
  },
  "cacheVariables": {
    // put any extra cache variables that you want in here
  }
}
```
**NOTE: set `inherits` to `carbon_osx_vcpkg` for MacOS development**

5. run `cmake -S . --preset local`
6. your project should be configured


- Insert any dependencies you might need into the `vcpkg.json` file
- Consult the [VCPKG documentation](https://learn.microsoft.com/en-us/vcpkg/) for more information on the VCPKG integration.

# CI Pipeline Documentation

This pipeline automates handling open pull requests (PRs) when new commits are pushed to the repository.

---

## **How It Works**

1. **Trigger on Commit**  
   - The pipeline runs automatically every time a commit is pushed to any branch.

2. **Check for Open PRs**  
   - It checks if the committed branch is the **target branch** of any open PRs.

3. **Filter PRs**  
   - The pipeline identifies and sorts PRs into two categories:
     - **Domestic PRs**: Source branch belongs to the same repository.
     - **International PRs**: Source branch comes from a forked repository.

4. **Dynamic Builds**  
   - For each PR, the pipeline creates a separate build step using GitHub Actions' **matrix logic** to handle PRs in parallel.

5. **Merge Logic**  
   - Merges the latest changes from the **target branch** (committed branch) into the **source branch** of each PR.
   - Pushes the updated source branch back:
     - Domestic PRs: Push to the same repo.
     - International PRs: Push to the forked repo.

6. **Conflict Handling**  
   - If there are merge conflicts that require manual resolution, the specific PR build step fails.

---

## **Notes**

- The pipeline is dynamic and scales with the number of open PRs.
- Conflicts must be resolved manually, and affected builds will display the failure in the CI logs.
- Parallel builds ensure efficient processing of multiple PRs.

This setup ensures all PRs remain up-to-date with the latest changes from the target branch.

## 📄 License and Legal Notices

© 2026 CCP Games 

This software is provided by CCP Games and does not include or distribute any third-party libraries or frameworks. 

Trademark Notice: CCP Games is a trademark of CCP ehf. 

This project is licensed under the [MIT License](LICENSE.md). Nothing in the [MIT License](LICENSE.md) grants any rights to CCP Games' trademarks or game content.