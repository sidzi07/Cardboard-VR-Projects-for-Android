precision mediump float;
uniform sampler2D u_Texture;

varying vec3 v_Position;
varying vec2 v_TexCoordinate;
uniform vec4 u_Color;
uniform float u_Width;

void main() {
    // send the color from the texture straight out unless in
    // border area
    if(
        v_TexCoordinate.x > u_Width
        && v_TexCoordinate.x < 1.0 - u_Width
        && v_TexCoordinate.y > u_Width
        && v_TexCoordinate.y < 1.0 - u_Width
    ){
        gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
    } else {
        gl_FragColor = u_Color;
    }
}
