uniform mat4 u_MVP;
uniform mat4 u_MV;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Position;
varying vec3 v_Normal;

void main() {
    // vertex in eye space
    v_Position = vec3(u_MV * a_Position);

    // normal's orientation in eye space
    v_Normal = vec3(u_MV * vec4(a_Normal, 0.0));

    // point in normalized screen coordinates
    gl_Position = u_MVP * a_Position;
}
