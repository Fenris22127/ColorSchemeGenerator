<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]-->



<!-- PROJECT LOGO -->
<br />
<div id="center">
  <a href="https://github.com/Fenris22127/ColorSchemeGenerator">
    <img src="src/res/images/logo.png" alt="Logo" width="100" height="76">
  </a>
  <a id="readme-top"></a>

<h1 id="center">Color Scheme Generator</h1>

  <p id="center">
    This program generates a color scheme from an image using K-Means clustering and allows the user to save it as a
    PDF file.
    <br />
    <!--<a href="https://github.com/github_username/repo_name"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/github_username/repo_name">View Demo</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Report Bug</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Request Feature</a>-->
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary style="font-size: 18px; margin-top: 20px;">Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li>
          <a href="#installation">Installation</a>
          <ol>
            <li><a href="#installing-java">Installing Java</a></li>
            <li></li>
          </ol>
        </li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
<h1>About The Project</h1>
<a name="about-the-project"></a>
<div id="center">
    <a id="center" href="https://github.com/Fenris22127/ColorSchemeGenerator">
        <img src="src/res/images/example.png" alt="Example" width="500">
    </a>
</div>
<p>
On a wonderful day, the 9th of August 2022 to be exact, during a wonderful holiday in Rostock, Germany to clear my mind from that summers extreme stress and all things uni, I decided that taking a break is for losers, only the strong shall survive and how better to practice my tolerance to constant exposure to stress than to start a coding project during my four days of vacation for the whole year?

So I stood there in the ocean, gently being rocked back and forth by the waves, intently staring at some water insect that had been trying to swim to me for several minutes at this point, when I decided to look back at the beach and noticed the white houses, green grass, blue ocean and golden sand. And I thought "How nice would it be, if I could paint this scene with exactly those colors?", well knowing, that I hadn't touched paint in ages and that landscape painting was absolutely not my thing.

But that did not matter. My mind had already started racing.

There were a few problems though. The WiFi of the place we were staying had died and so did my high speed, leaving me with no quick way to read up on anything related to creating a color scheme from an image.

So there I was, armed with only my phone, my tablet I used for drawing, OneNote and my mind going at 100 km/h. I started thinking and writing wild concepts and ideas, until some sort of idea had formed. And because planning is for people who don't know where they are going, I started writing code, since I definitely absolutely knew what I was doing and where I was going.

I remembered my professor's words, that we needed to know how to write code on paper as we wouldn't always have a computer and an IDE available and I cursed my arrogance as I had thought, that if I needed to code, I would use my PC or an online IDE and if I had neither available, I would just not code. So I had to fall back onto my very limited memory of my first programming classes. Lucky for me, due to my bad memory, I write everything down so I had at least some code snippets I could use as reference for my first lines of code.

I spent my holiday reading up on K-Means Clustering, three dimensional spaces in Java and different forms of visualizations of color spaces. And if I am honest, those holidays were amazing. There is a difference between sitting at home and researching and sitting in the sun at the beach in a different city and researching.

When I had come home, I transferred my code into my IDE and to my surprise and joy, I had only very few issues to fix until the version 1.0 of my Color Scheme Generator was working.

Since then, I incorporated the iTextPDF library to be able to save the resulting color scheme, I became friends or at least decided to call a truce with gradle to better manage my dependencies and recently even cautiously approached maths to add a color wheel to the file.

I have also learned a ton about Java, gained a lot of experience with managing a project, Code Conventions and most importantly patience and determination and can finally say: I am incredibly proud of my project. It might not be a revolutionary tool that people are in dire need of but it is my first large project that I have kept up with and improved as I continued my studies and that is the result of my determination, patience with myself and my love for coding, which I discovered a few months into my first semester at the University of Applied Sciences in Wernigerode, Germany.
</p>


<!-- Here's a blank template to get started: To avoid retyping too much info. Do a search and replace with your text editor for the following: `github_username`, `repo_name`, `twitter_handle`, `linkedin_username`, `email_client`, `email`, `project_title`, `project_description` -->

<p id="right"><button><a href="#readme-top">Back to top</a></button></p>



## Built With

[![Java][Java.de]][Java-url]
<!-- * [![React][React.js]][React-url]
* [![Vue][Vue.js]][Vue-url]
* [![Angular][Angular.io]][Angular-url]
* [![Svelte][Svelte.dev]][Svelte-url]
* [![Laravel][Laravel.com]][Laravel-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url] -->



<!-- GETTING STARTED -->
# Getting Started

To get a local copy up and running follow these steps.

## Prerequisites

This program requires Java 18+ to run properly. How to install Java will be explained in the following steps. If your device is not running on a 64-bit operating system, you cannot use this program. You will also need administrator privileges to install Java.

## Installation

### Installing Java
1. Find out which bit-version your operating system is running on:

    * #### Windows 11:
      1. Open your settings
      2. Open the `About` page in the `System` tab
      3. You will see your bit version in the `Device Specifications` tab under `System type`<br>
        <b>Example:</b>
        <code><b>64-bit</b> operating system, <b>x64</b>-based processor</code>
    * #### Windows 10:
      1. Open your settings
      2. Open the `System` page, then click on `About`
      3. You will see your bit version in the `Device Specifications` tab under `System type`, below the Windows Defender details<br>
        <b>Example:</b>
        <code><b>64-bit</b> operating system, <b>x64</b>-based processor</code>
    * #### Mac (until Version 10.11):
      1. Click on the Apple Logo in the upper left corner
      2. Open the `About This Mac` page
      3. You will see your processor below the operating system and computer model name. If your processor appears on the following list, your device is running on a <b>64-bit</b> operating system. If you have a processor not listed here, your device is running on a <b>32-bit</b> operating system.
          * Core 2 Duo
          * Dual-core Xeon
          * Quad-core Xeon
          * Core i3
          * Core i5
          * Core i7
    * #### Mac (Version 10.11+):
      1. Open your Spotlight, search `Terminal` and open it
      2. You should now see two lines of text: your last login and in line 2 `[device name]:~ [username]$`
      3. Place your curser at the end of line 2 (make sure to leave a whitespace between the cursor and the `$`)
      4. Type `getconf LONG_BIT` and hit Enter
      5. The number being displayed in the following line is the bit-version of your device
    * #### Linux:
      1. Press `Ctrl + Alt + T` to open the terminal
      2. Type `getconf LONG_BIT` and hit Enter
      3. The number being displayed in the following line is the bit-version of your device

2. Install Java from the [Oracle Archive](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html) by downloading the following
  * Windows: `Windows x64 Installer`
  * Mac: `macOS x64 DMG Installer`
  * Linux: `Linux x64 Compressed Archive`

3. Execute the JDK Installer by double-clicking it, click `yes` if it requests system permission

4. Now, the Installer Welcome Screen should open. Click on `Next` to change the installation location if required. Click `Next` again to start the installation. After successfully installing Java, you can click `Close`.


<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- ROADMAP -->
## Roadmap

- [ ] Feature 1
- [ ] Feature 2
- [ ] Feature 3
    - [ ] Nested Feature

See the [open issues](https://github.com/github_username/repo_name/issues) for a full list of proposed features (and known issues).

<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- CONTACT -->
## Contact

Your Name - [@twitter_handle](https://twitter.com/twitter_handle) - email@email_client.com

Project Link: [https://github.com/github_username/repo_name](https://github.com/github_username/repo_name)

<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []()
* []()
* []()

<p align="right"><button><a href="#readme-top">Back to top</a></button></p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/github_username/repo_name.svg?style=for-the-badge
[contributors-url]: https://github.com/github_username/repo_name/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/github_username/repo_name.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo_name/network/members
[stars-shield]: https://img.shields.io/github/stars/github_username/repo_name.svg?style=for-the-badge
[stars-url]: https://github.com/Fenris_22127/ColorSchemeGenerator/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo_name.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/repo_name/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/github_username/repo_name/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/linkedin_username
[product-screenshot]: images/screenshot.png
<!-- [Next.js]: https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vue.js]: https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D
[Vue-url]: https://vuejs.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/ -->
[Java-url]: https://www.java.com/
[Java.de]: https://img.shields.io/badge/java-41492f?style=for-the-badge&logoColor=fafbfc
[Close-x]: https://img.shields.io/badge/%E2%9C%95-red?style=flat&logoColor=green
<!-- [Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Laravel.com]: https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white
[Laravel-url]: https://laravel.com
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com -->

<style>
  #center {
    text-align: center;
  }
  #right {
    text-align: right;
  }

  button {
    background-color: #c2fbd7;
    border-radius: 100px;
    box-shadow: 
          rgba(44,187,99,.35) 0 -25px 18px -14px inset,
          rgba(44,187,99,.25) 0 1px 2px,
          rgba(44,187,99,.25) 0 2px 4px;
    color: green;
    cursor: pointer;
    display: inline-block;
    /*font-family: CerebriSans-Regular,-apple-system,system-ui,Roboto,sans-serif;*/
    padding: 7px 20px;
    text-align: center;
    text-decoration: none;
    transition: all 250ms;
    border: 0;
    font-size: 11px;
    user-select: none;
    -webkit-user-select: none;
    touch-action: manipulation;
  }
  button a {
      color: green;
  }
  h1 {
    margin: 20px 0 5px 0;
  }
</style>