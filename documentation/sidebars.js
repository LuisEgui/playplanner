/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  // By default, Docusaurus generates a sidebar from the docs folder structure
  //mainSidebar: [{type: 'autogenerated', dirName: '.'}],

  // But you can create a sidebar manually

  mainSidebar: [
    {
      type: 'category',
      label: 'Quick Start',
      items: [
        'quick-start/try-dunktomic-in-docker',
        'quick-start/baremetal-deployment'],
    },
    {
      type: 'category',
      label: 'Main Concepts',
      items: ['main-concepts/high-level-design']
    }
  ],
};

export default sidebars;
