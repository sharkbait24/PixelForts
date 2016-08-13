# PixelForts

  Creator: Joe Coleman
  
  Email: cpaul@pdx.edu
  

  
  Pixel Forts is a simple Android game that mimics the old tank wars.  There are up to 5 forts on the map that can shoot at each other until only one remains.  Currently this game is in early alpha, and the game automatically starts with all 5 players in roughly predefined positions.  The players only have a single gun at the moment but they can touch the screen to aim in that direction and then press and hold the "button" (currently not displayed) in the bottom right corner of the game to charge the weapon and fire.  Shooting a fort causes it to take damage, and when enough is sustained that player is dead.  The game will cycle through the players until only one, or none, remain and then stop.
  
  This game uses a custom game engine that I built, and as such there are limitations and bugs.  Currently the engine only knows how to render rectangle objects, and collision detection only has two types, rectangles and circles.  The engine uses OpenGL for rendering, which is probably not the best idea for a 2D game, but I wanted to learn.  The engine has three threads, the main Android UI thread, the GameView thread which runs the game loop, and the OpenGL rendering thread.  Currently all of the objects in the game either directly extend, or contain a Transform, which keeps track of the object's position in the world.  These transforms are also what almost everything expects to deal with, and as such should be included in any object you want to place in the world.
  
  Going forward I will likely perform a major revamp of the core engine.  For a roadmap of what I intend to do, please check out the Wiki.  This code and software is provided under the MIT license, see the COPYING.txt file for details.  Please feel free to use it and/or contribute.
  
  Thanks,
  
  Joe
  
      **REQUIREMENTS**
  Android API >= 9 (2.3 Gingerbread)
  
  OpenGL 2.0 capable device
  
  
