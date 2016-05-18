uniform mat4 u_MVP;
uniform mat4 u_MV;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoordinate;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main() {
   // vertex to eye space
   v_Position = vec3(u_MV * a_Position);

   // pass through the texture coordinate
   v_TexCoordinate = a_TexCoordinate;

   // normal's orientation in eye space
   v_Normal = vec3(u_MV * vec4(a_Normal, 0.0));

   // final point in normalized screen coordinates
   gl_Position = u_MVP * a_Position;
}
