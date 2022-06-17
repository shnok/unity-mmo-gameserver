*NOTE: THE TRAIL2 EXAMPLE REUIRES UNITY PRO TO GET THE BEST RESULT*
*You can tweak the Displacement Strength parameter in the slash01_distort material to adjust the Trial2's displacement effect*

Version - 1.0.1
Version Changes:
1.0.1
-Fixed a null reference bug when scene changed.
1.1.0
-No mesh object created.
-No GC allocated!
-Huge performance improved.
1.1.1
-Added "Use With 2D" option to support with 2d sprite.
1.1.2
-Fixed a null reference error in OnDestroy() function.

How to use:
1, Find the weapon socket in your animated model.
2, Drag the "X-WeaponTrail.prefab" into its hierarchy.
3, Adjust the "StartPoint" and "EndPoint"'s position to match the weapon's length.
4, Now play the animation you should see a smooth trail.

API:
Activate(): Activate trail
Deactivate(): Stop trail immediately
StopSmoothly(float fadeTime): Stop trail smoothly.
*NOTE YOU NEED TO IMPORT THE NAME SPACE, for example, using Xft; in C#*

Parameters
Max Frame: Indicates the length of the trail.
Granularity: Indicates the granularity of the trail, the bigger the smoother.
Fps: Indicates the update frequence of the trail.

Video Tutorial
https://www.youtube.com/watch?v=1lQ7p2OQsA4


More Effects:
Please check my new FX package, Hope you like it.
http://phantomparticle.org/

Contact
shallwaycn@gmail.com