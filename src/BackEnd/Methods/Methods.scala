package BackEnd.Methods

import BackEnd.scalaClasses.{Player, Projectile, theWorld}

object Methods {


  def hitDetection(player: Player, projectile: Projectile): Boolean = {
    // returns true if there is collision
    if(player.x > projectile.x + projectile.sizeX || projectile.x > player.x + player.sizeX){
      false
    }else if(player.y > projectile.y + projectile.sizeY || projectile.y > player.y + player.sizeY){
      false
    }else{
      true
    }
  }


  def offMapDetection(player: Player, world: theWorld): Boolean = {
    // returns true if the player is out of bounds
    if(player.x < 0 || player.x > world.boundX){
      true
    }else if(player.y < 0 || player.y > world.boundY){
      true
    }else{
      false
    }
  }

  /**/




}
